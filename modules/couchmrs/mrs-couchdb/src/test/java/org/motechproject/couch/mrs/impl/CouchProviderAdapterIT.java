package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchPersons;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.couch.mrs.repository.impl.AllCouchPersonsImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchProvidersImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchProviderAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchProviderAdapter providerAdapter;

    @Autowired
    private AllCouchProviders allProviders;

    @Autowired
    private AllCouchPersons allPersons;

    @Autowired
    private EventListenerRegistryService eventListenerRegistry;

    private Initializer init;
    private MrsListener mrsListener;
    final Object lock = new Object();

    @Autowired
    @Qualifier("couchProviderDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PROVIDER_SUBJECT, EventKeys.DELETED_PROVIDER_SUBJECT, EventKeys.UPDATED_PROVIDER_SUBJECT));
    }

    @Test
    public void shouldSaveAProviderAndRetrieveByProviderId() throws MRSCouchException, InterruptedException {
        CouchPerson person = init.initializePerson1();

        CouchProvider provider = new CouchProvider("providerId", person);

        synchronized (lock) {
            providerAdapter.saveProvider(provider);
            lock.wait(60000);
        }

        MRSProvider retrievedProvider = providerAdapter.getProviderByProviderId("providerId");

        assertEquals(retrievedProvider.getProviderId(), "providerId");
        assertNotNull(retrievedProvider.getPerson());

        assertEquals(retrievedProvider.getProviderId(), mrsListener.eventParameters.get(EventKeys.PROVIDER_ID));
        assertEquals(retrievedProvider.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deleted);
        assertFalse(mrsListener.updated);
    }

    @Test
    public void shouldRemoveProviderWithNoPerson() throws InterruptedException {
        createAndRemoveProvider(null, true);
    }

    @Test
    public void shouldRemoveProviderWithPerson() throws MRSCouchException, InterruptedException {
        CouchPerson person = init.initializePerson1();

        createAndRemoveProvider(person, false);
    }

    private void createAndRemoveProvider(CouchPerson person, boolean isNull) throws InterruptedException {

        CouchProvider provider = new CouchProvider("providerId", person);

        synchronized (lock) {
            providerAdapter.saveProvider(provider);
            lock.wait(60000);
        }

        MRSProvider retrievedProvider = providerAdapter.getProviderByProviderId("providerId");

        assertEquals(retrievedProvider.getProviderId(), "providerId");

        if (isNull) {
            assertNull(retrievedProvider.getPerson());
        } else {
            assertNotNull(retrievedProvider.getPerson());
            assertEquals(retrievedProvider.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        }

        assertEquals(retrievedProvider.getProviderId(), mrsListener.eventParameters.get(EventKeys.PROVIDER_ID));
        assertTrue(mrsListener.created);

        synchronized (lock) {
            providerAdapter.removeProvider("providerId");
            lock.wait(60000);
        }
        assertNull(providerAdapter.getProviderByProviderId("providerId"));
        assertTrue(mrsListener.created);
        assertTrue(mrsListener.deleted);
        assertFalse(mrsListener.updated);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchProvidersImpl) allProviders).removeAll();
        ((AllCouchPersonsImpl) allPersons).removeAll();
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private boolean updated = false;
        private boolean deleted = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_PROVIDER_SUBJECT, EventKeys.UPDATED_PROVIDER_SUBJECT, EventKeys.DELETED_PROVIDER_SUBJECT})
        public void handle(MotechEvent event) {
            if (event.getSubject().equals(EventKeys.CREATED_NEW_PROVIDER_SUBJECT)) {
                created = true;
            } else if (event.getSubject().equals(EventKeys.UPDATED_PROVIDER_SUBJECT)) {
                updated = true;
            } else if (event.getSubject().equals(EventKeys.DELETED_PROVIDER_SUBJECT)) {
                deleted = true;
            }
            eventParameters = event.getParameters();
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public String getIdentifier() {
            return "mrsTestListener";
        }
    }
}
