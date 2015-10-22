// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import ca.uhn.hl7v2.model.v24.segment.ROL;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.valueset.PractitionerRoleEnum;
import com.datafascia.etl.event.AddObservations;
import com.datafascia.etl.event.AddParticipant;
import com.datafascia.etl.event.AdmitPatient;
import com.datafascia.etl.event.DischargePatient;
import com.datafascia.etl.hl7.GenderFormatter;
import com.google.common.base.Strings;
import java.util.List;
import javax.inject.Inject;

/**
 * Base class for ADT message processor.
 */
public abstract class AdmitDischargeProcessor extends BaseProcessor {

  private static final String OBX_ROOT_PATH = "/OBX" + SUBSCRIPT_PLACEHOLDER;

  @Inject
  private AdmitPatient admitPatient;

  @Inject
  private AddParticipant addParticipant;

  @Inject
  private DischargePatient dischargePatient;

  @Inject
  private AddObservations addObservations;

  private UnitedStatesPatient toPatient(PID pid) throws HL7Exception {
    XPN patientName = pid.getPatientName(0);

    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT)
        .setValue(getPatientIdentifier(pid));
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT)
        .setValue(pid.getPatientAccountNumber().getID().getValue());
    patient.addName()
        .addGiven(patientName.getGivenName().getValueOrEmpty())
        .addGiven(patientName.getSecondAndFurtherGivenNamesOrInitialsThereof().getValueOrEmpty())
        .addFamily(patientName.getFamilyName().getSurname().getValueOrEmpty());
    patient.addCommunication()
        .setPreferred(true)
        .setLanguage(toLanguage(pid.getPrimaryLanguage().getIdentifier().getValue()));
    patient
        .setRace(toRace(pid.getRace(0).getIdentifier().getValue()))
        .setGender(GenderFormatter.parse(pid.getAdministrativeSex().getValue()))
        .setBirthDate(toDate(pid.getDateTimeOfBirth()))
        .setMaritalStatus(toMaritalStatus(pid.getMaritalStatus().getIdentifier().getValue()))
        .setActive(true);
    return patient;
  }

  private Location toLocation(PV1 pv1) throws HL7Exception {
    String assignedPatientLocation = pv1.getAssignedPatientLocation().encode();
    Location location = new Location()
        .setName(assignedPatientLocation);
    location.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_LOCATION)
        .setValue(assignedPatientLocation);
    return location;
  }

  private Encounter toEncounter(PV1 pv1) throws HL7Exception {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER)
        .setValue(getEncounterIdentifier(pv1));

    PeriodDt period = new PeriodDt()
        .setStart(toDateTime(pv1.getAdmitDateTime()))
        .setEnd(toDateTime(pv1.getDischargeDateTime(0)));
    encounter.setPeriod(period);
    return encounter;
  }

  private HumanNameDt toHumanName(XCN xcn) throws HL7Exception {
    HumanNameDt humanName = new HumanNameDt();

    if (!xcn.getGivenName().isEmpty()) {
      humanName.addGiven(xcn.getGivenName().getValue());
    }

    if (!xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().isEmpty()) {
      humanName.addGiven(xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue());
    }

    if (!xcn.getFamilyName().isEmpty()) {
      humanName.addFamily(xcn.getFamilyName().getSurname().getValue());
    }

    return humanName;
  }

  private Practitioner toPractitioner(ROL rol) throws HL7Exception {
    XCN rolePerson = rol.getRolePerson(0);

    Practitioner practitioner = new Practitioner()
        .setName(toHumanName(rolePerson));
    practitioner.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PRACTITIONER)
        .setValue(rolePerson.getIDNumber().getValue());

    practitioner.addPractitionerRole().getRole().addCoding()
        .setSystem(PractitionerRoleEnum.PRIMARY_ATTENDING.getSystem())
        .setCode(rol.getRoleROL().getIdentifier().getValue());

    return practitioner;
  }

  private Encounter.Participant toParticipant(ROL rol) throws HL7Exception {
    PeriodDt period = new PeriodDt()
        .setStart(toDateTime(rol.getRoleBeginDateTime()))
        .setEnd(toDateTime(rol.getRoleEndDateTime()));
    Encounter.Participant participant = new Encounter.Participant()
        .setPeriod(period);
    return participant;
  }

  /**
   * Admits patient.
   *
   * @param msh
   *     MSH segment
   * @param pid
   *     PID segment
   * @param pv1
   *     PV1 segment
   * @param rolList
   *     ROL list
   * @throws HL7Exception if HL7 message is malformed
   */
  protected void admitPatient(MSH msh, PID pid, PV1 pv1, List<ROL> rolList) throws HL7Exception {
    UnitedStatesPatient patient = toPatient(pid);
    Location location = toLocation(pv1);
    Encounter encounter = toEncounter(pv1);
    admitPatient.accept(
        msh.getMessageType().getTriggerEvent().getValue(), patient, location, encounter);

    for (ROL rol : rolList) {
      Practitioner practitioner = toPractitioner(rol);
      Encounter.Participant participant = toParticipant(rol);
      addParticipant.accept(practitioner, participant, encounter);
    }
  }

  /**
   * Discharges patient.
   *
   * @param msh
   *     MSH segment
   * @param pv1
   *     PV1 segment
   * @throws HL7Exception if HL7 message is malformed
   */
  protected void dischargePatient(MSH msh, PV1 pv1) throws HL7Exception {
    Encounter encounter = toEncounter(pv1);
    dischargePatient.accept(msh.getMessageType().getTriggerEvent().getValue(), encounter);
  }

  /**
   * Adds any OBX segments in the HL7 message as observations.
   *
   * @param message
   *     HL7 message
   * @param pid
   *     PID segment
   * @param pv1
   *     PV1 segment
   * @throws HL7Exception if HL7 message is malformed
   */
  protected void addObservations(Message message, PID pid, PV1 pv1) throws HL7Exception {
    // See if OBX segment exists.
    Terser terser = new Terser(message);
    if (Strings.isNullOrEmpty(terser.get(OBX_ROOT_PATH.replace(SUBSCRIPT_PLACEHOLDER, "")
        + "-1"))) {
      return;
    }

    List<Observation> observations = toObservations(terser, OBX_ROOT_PATH, "");
    addObservations.accept(
        observations, getPatientIdentifier(pid), getEncounterIdentifier(pv1));
  }
}
