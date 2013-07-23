package org.motechproject.couch.mrs.repository.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.model.MotechIdReservation;
import org.motechproject.couch.mrs.repository.AllMotechIdReservations;
import org.motechproject.mrs.exception.MRSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMotechIdReservationsImpl extends MotechBaseRepository<MotechIdReservation> implements AllMotechIdReservations {

    @Autowired
    protected AllMotechIdReservationsImpl(@Qualifier("motechIdReservationDatabaseConnector") CouchDbConnector db) {
        super(MotechIdReservation.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_motechId", map = "function(doc) { if (doc.type ==='MotechIdReservation') { emit(doc._id, doc._id); }}")
    public MotechIdReservation findByMotechId(String motechId) {
        if (motechId == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_motechId").key(motechId).includeDocs(true);
        return getFirst(db.queryView(viewQuery, MotechIdReservation.class));
    }

    @Override
    public void addMotechIdReservation(MotechIdReservation motechIdReservation) {

        MotechIdReservation motechIdReservationToUpdate = findByMotechId(motechIdReservation.getId());

        if (motechIdReservationToUpdate != null) {
            throw new MRSException("Reservation for MotechID = '" + motechIdReservation.getId() + "' already exists.");
        }

        try {
            super.add(motechIdReservation);
        } catch (IllegalArgumentException e) {
            throw new MRSCouchException(e.getMessage(), e);
        }
    }

    private MotechIdReservation getFirst(List<MotechIdReservation> motechIdReservations) {
        if (motechIdReservations == null || motechIdReservations.isEmpty()) {
            return null;
        } else {
            return motechIdReservations.get(0);
        }
    }
}
