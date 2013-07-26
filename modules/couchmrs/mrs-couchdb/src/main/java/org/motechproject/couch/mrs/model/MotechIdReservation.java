package org.motechproject.couch.mrs.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'MotechIdReservation'")
public class MotechIdReservation extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    private final String type = "MotechIdReservation";

    public MotechIdReservation() {
        super();
        this.setType(type);
    }

    public MotechIdReservation(String motechId) {
        this();
        setId(motechId);
    }
}
