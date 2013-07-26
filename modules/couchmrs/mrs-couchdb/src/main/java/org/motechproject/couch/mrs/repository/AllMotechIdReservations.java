package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.MotechIdReservation;

public interface AllMotechIdReservations {

    MotechIdReservation findByMotechId(String motechId);

    void addMotechIdReservation(MotechIdReservation motechIdReservation);

    void remove(MotechIdReservation motechIdReservation);

}
