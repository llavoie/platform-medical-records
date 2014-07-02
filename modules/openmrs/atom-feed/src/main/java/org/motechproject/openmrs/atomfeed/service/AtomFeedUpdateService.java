package org.motechproject.openmrs.atomfeed.service;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.openmrs.atomfeed.repository.AtomFeedUpdate;

import java.util.List;

public interface AtomFeedUpdateService extends MotechDataService<AtomFeedUpdate> {

    @Lookup(name="by_time")
    List<AtomFeedUpdate> getByLastUpdateTime(@LookupField(name="lastUpdateTime")String lastUpdateTime);

    @Lookup(name="by_id")
    List<AtomFeedUpdate> getByLastId(@LookupField(name="lastId")String lastId);

}