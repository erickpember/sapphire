// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import ca.uhn.hl7v2.model.v24.segment.ROL;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.valueset.PractitionerRoleEnum;
import com.datafascia.etl.event.AddObservations;
import com.datafascia.etl.event.AddParticipant;
import com.datafascia.etl.event.AdmitPatient;
import com.datafascia.etl.event.DischargePatient;
import com.datafascia.etl.hl7.GenderFormatter;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Base class for ADT message processor.
 */
public abstract class AdmitDischargeProcessor extends BaseProcessor {

  private static final String OBX_PATH_PATTERN = "/OBX(%2$d)";
  private static final String NTE_PATH_PATTERN = null;

  @Inject
  private Clock clock;

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
        .setBirthDate(TimeStamps.toDate(pid.getDateTimeOfBirth()))
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
        .setStart(TimeStamps.toDateTime(pv1.getAdmitDateTime()))
        .setEnd(TimeStamps.toDateTime(pv1.getDischargeDateTime(0)));
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

  private Practitioner toPractitioner(XCN person, String roleCode) throws HL7Exception {
    Practitioner practitioner = new Practitioner()
        .setName(toHumanName(person));
    practitioner.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PRACTITIONER)
        .setValue(person.getIDNumber().getValue());

    practitioner.addPractitionerRole().getRole().addCoding()
        .setSystem(PractitionerRoleEnum.PRIMARY_CARE_ATTENDING.getSystem())
        .setCode(roleCode);

    return practitioner;
  }

  private Encounter.Participant toParticipant(
      DateTimeDt start, DateTimeDt end) throws HL7Exception {

    PeriodDt period = new PeriodDt()
        .setStart(start)
        .setEnd(end);
    Encounter.Participant participant = new Encounter.Participant()
        .setPeriod(period);
    return participant;
  }

  private void readPrimaryCareAttending(PV1 pv1, Encounter encounter) throws HL7Exception {
    XCN attending = pv1.getAttendingDoctor(0);
    if (!attending.isEmpty()) {
      Practitioner practitioner = toPractitioner(
          attending, PractitionerRoleEnum.PRIMARY_CARE_ATTENDING.getCode());

      Instant now = Instant.now(clock);
      DateTimeDt start = new DateTimeDt(Date.from(now));
      Encounter.Participant participant = toParticipant(start, null);
      addParticipant.accept(practitioner, participant, encounter);
    }
  }

  private Practitioner toPractitioner(ROL rol) throws HL7Exception {
    return toPractitioner(rol.getRolePerson(0), rol.getRoleROL().getIdentifier().getValue());
  }

  private Encounter.Participant toParticipant(ROL rol) throws HL7Exception {
    return toParticipant(
        TimeStamps.toDateTime(rol.getRoleBeginDateTime()),
        TimeStamps.toDateTime(rol.getRoleEndDateTime()));
  }

  private void readParticipants(List<ROL> rolList, Encounter encounter) throws HL7Exception {
    for (ROL rol : rolList) {
      Practitioner practitioner = toPractitioner(rol);
      Encounter.Participant participant = toParticipant(rol);
      addParticipant.accept(practitioner, participant, encounter);
    }
  }

  /**
   * Admits, transfers, updates patient.
   *
   * @param message
   *     HL7 message
   * @param msh
   *     MSH segment
   * @param pid
   *     PID segment
   * @param pv1
   *     PV1 segment
   * @param rolList
   *     ROL list
   * @param rol2List
   *     ROL list
   * @throws HL7Exception if HL7 message is malformed
   */
  protected void admitPatient(
      Message message, MSH msh, PID pid, PV1 pv1, List<ROL> rolList, List<ROL> rol2List)
      throws HL7Exception {

    UnitedStatesPatient patient = toPatient(pid);
    Location location = toLocation(pv1);
    Encounter encounter = toEncounter(pv1);
    admitPatient.accept(
        msh.getMessageType().getTriggerEvent().getValue(),
        TimeStamps.toDateTime(msh.getDateTimeOfMessage()),
        patient,
        location,
        encounter);

    readPrimaryCareAttending(pv1, encounter);
    readParticipants(rolList, encounter);
    readParticipants(rol2List, encounter);

    addObservations(message, pid, pv1);
  }

  /**
   * Discharges patient.
   *
   * @param message
   *     HL7 message
   * @param msh
   *     MSH segment
   * @param pid
   *     PID segment
   * @param pv1
   *     PV1 segment
   * @param rolList
   *     ROL list
   * @param rol2List
   *     ROL list
   * @throws HL7Exception if HL7 message is malformed
   */
  protected void dischargePatient(
      Message message, MSH msh, PID pid, PV1 pv1, List<ROL> rolList, List<ROL> rol2List)
      throws HL7Exception {

    addObservations(message, pid, pv1);

    Encounter encounter = toEncounter(pv1);
    readParticipants(rolList, encounter);
    readParticipants(rol2List, encounter);

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
    ObservationsBuilder observationsBuilder =
        new ObservationsBuilder(message, OBX_PATH_PATTERN, NTE_PATH_PATTERN);
    if (observationsBuilder.hasObservations()) {
      List<Observation> observations = observationsBuilder.toObservations();
      addObservations.accept(
          observations, getPatientIdentifier(pid), getEncounterIdentifier(pv1));
    }
  }
}
