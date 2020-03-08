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
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Location;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.util.List;
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

    Location unOriginalLocation = createLocation("point-of-care2^room2^bed2");
    locationRepository.save(unOriginalLocation);

    Id<Location> locationId = Id.of(originalLocation.getId().getIdPart());
    Location location = locationRepository.read(locationId).get();
    assertEquals(location.getName(), originalLocation.getName());

    List<Location> locations = locationRepository.list();
    assertEquals(locations.size(), 2);
  }
}
