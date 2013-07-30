package org.motechproject.couch.mrs.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@TypeDiscriminator("doc.type === 'Patient'")
public class CouchPatientImpl extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private String facilityId;

    @JsonProperty
    private String personId;
    @JsonProperty
    private String motechId;

    private final String type = "Patient";

    public CouchPatientImpl() {
        super();
        this.setType(type);
    }

    public CouchPatientImpl(String patientId, String motechId, String personId, String facilityId) {
        this();
        if (patientId != null) {
            setId(patientId);
        }
        this.motechId = motechId;
        this.personId = personId;
        this.facilityId = facilityId;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPatientId() {
        return getId();
    }

    public void setPatientId(String patientId) {
        setId(patientId);
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
