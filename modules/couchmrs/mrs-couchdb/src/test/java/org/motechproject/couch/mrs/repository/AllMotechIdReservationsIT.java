package org.motechproject.couch.mrs.repository;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.model.MotechIdReservation;
import org.motechproject.couch.mrs.repository.impl.AllMotechIdReservationsImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllMotechIdReservationsIT extends SpringIntegrationTest {

    @Autowired
    private AllMotechIdReservations allMotechIdReservations;

    @Autowired
    @Qualifier("motechIdReservationDatabaseConnector")
    private CouchDbConnector connector;

    @Test
    public void shouldSavePatientAndRetrieveById() throws MRSCouchException {
        MotechIdReservation motechIdReservation = new MotechIdReservation("MotechID");

        allMotechIdReservations.addMotechIdReservation(motechIdReservation);

        MotechIdReservation motechIdReservationRetrieved = allMotechIdReservations.findByMotechId("MotechID");

        assertEquals(motechIdReservationRetrieved.getId(), "MotechID");
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllMotechIdReservationsImpl) allMotechIdReservations).removeAll();
    }
}
