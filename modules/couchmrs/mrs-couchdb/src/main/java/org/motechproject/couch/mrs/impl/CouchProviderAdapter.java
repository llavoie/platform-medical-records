package org.motechproject.couch.mrs.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.CouchProviderImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchPersons;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.couch.mrs.util.CouchMRSConverterUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSProviderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouchProviderAdapter implements MRSProviderAdapter {

    @Autowired
    private AllCouchProviders allCouchProviders;

    @Autowired
    private AllCouchPersons allCouchPersons;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public MRSProvider saveProvider(MRSProvider provider) {

        Boolean updated = (getProviderByProviderId(provider.getProviderId()) == null) ? false : true;

        MRSPerson person = provider.getPerson();

        try {
            String personId = null;

            if (person != null) {
                CouchPerson couchPerson = CouchMRSConverterUtil.convertPersonToCouchPerson(provider.getPerson());
                allCouchPersons.addPerson(couchPerson);
                personId = person.getPersonId();
            }
            allCouchProviders.addProvider(new CouchProviderImpl(provider.getProviderId(), personId));
            if (!updated) {
                eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PROVIDER_SUBJECT, EventHelper.providerParameters(provider)));
            } else {
                eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_PROVIDER_SUBJECT, EventHelper.providerParameters(provider)));
            }
        } catch (MRSCouchException e) {
            return null;
        }

        return new CouchProvider(provider.getProviderId(), person);
    }

    @Override
    public MRSProvider getProviderByProviderId(String motechId) {
        List<CouchProviderImpl> providers = allCouchProviders.findByProviderId(motechId);

        if (providers != null && providers.size() > 0) {
            CouchPerson person = null;
            String personId = providers.get(0).getPersonId();

            if (!StringUtils.isBlank(personId)) {
                List<CouchPerson> personList = allCouchPersons.findByPersonId(personId);
                if (personList.size() > 0) {
                    person = personList.get(0);
                }
            }

            return new CouchProvider(providers.get(0).getProviderId(), person);
        }

        return null;
    }

    @Override
    public void removeProvider(String motechId) {
        List<CouchProviderImpl> providerToRemove = allCouchProviders.findByProviderId(motechId);

        if (providerToRemove != null && providerToRemove.size() > 0) {
            CouchProviderImpl couchProvider = providerToRemove.get(0);

            CouchPerson personToRemove = null;

            if (!StringUtils.isBlank(couchProvider.getPersonId())) {
                List<CouchPerson> personList = allCouchPersons.findByPersonId(couchProvider.getPersonId());
                if (personList.size() > 0) {
                    personToRemove = personList.get(0);
                    allCouchPersons.remove(personToRemove);
                }
            }

            CouchProvider provider = new CouchProvider(providerToRemove.get(0).getProviderId(), personToRemove);
            allCouchProviders.remove(providerToRemove.get(0));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_PROVIDER_SUBJECT, EventHelper.providerParameters(provider)));
        }
    }
}
