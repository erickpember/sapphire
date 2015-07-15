// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Location;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.IdentifierSystems;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link LocationRepository} test
 */
public class LocationRepositoryTest extends RepositoryTestSupport {

  @Inject
  private LocationRepository locationRepository;

  private Location createLocation(String identifier) {
    Location location = new Location()
        .setName(identifier);
    location.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_LOCATION).setValue(identifier);
    return location;
  }

  @Test
  public void should_read_location() {
    Location originalLocation = createLocation("point-of-care^room^bed");
    locationRepository.save(originalLocation);

    Id<Location> locationId = Id.of(originalLocation.getId().getIdPart());
    Location location = locationRepository.read(locationId).get();
    assertEquals(location.getName(), originalLocation.getName());
  }
}
