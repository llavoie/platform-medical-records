package org.motechproject.couch.mrs.model;

import java.util.Iterator;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mrs.domain.MRSObservation;

@TypeDiscriminator("doc.type === 'Encounter'")
public class CouchEncounterImpl extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    private String encounterId;
    private String providerId;
    private String creatorId;
    private String facilityId;
    @JsonProperty("encounterDate")
    private DateTime date;
    private Set<CouchObservation> observations;
    @JsonProperty("encounterPatientId")
    private String patientId;
    private String encounterType;

    private final String type = "Encounter";

    public CouchEncounterImpl() {
        super();
        this.setType(type);
    }

    public CouchEncounterImpl(String encounterId, String providerId, String creatorId, // NO CHECKSTYLE ParameterNumber
                              String facilityId, DateTime date, Set<CouchObservation> observations, String patientId,
                              String encounterType) {
        this();
        this.encounterId = encounterId;
        this.providerId = providerId;
        this.creatorId = creatorId;
        this.facilityId = facilityId;
        this.date = date;
        this.observations = observations;
        this.patientId = patientId;
        this.encounterType = encounterType;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public DateTime getDate() {
        return DateUtil.setTimeZoneUTC(date);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Set<CouchObservation> getObservations() {
        return observations;
    }

    public void setObservations(Set<CouchObservation> observations) {
        this.observations = observations;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public String getType() {
        return type;
    }

    public MRSObservation getObservationById(String observationId) {
        MRSObservation returnObs = null;

        if (observations != null) {
            for (MRSObservation obs : observations) {
                if (obs.getObservationId().equals(observationId)) {
                    return obs;
                } else {
                    MRSObservation dependantObs = traverseDependantObservations(obs, observationId);

                    if (dependantObs != null) {
                        return dependantObs;
                    }
                }
            }
        }

        return returnObs;
    }

    private MRSObservation traverseDependantObservations(MRSObservation obs, String targetObsId) {
        if (obs.getDependantObservations() == null) {
            return null;
        }

        Iterator<? extends MRSObservation> iterator = obs.getDependantObservations().iterator();

        while (iterator.hasNext()) {
            MRSObservation dependantObs = iterator.next();
            if (dependantObs.getObservationId().equals(targetObsId)) {
                return dependantObs;
            } else {
                MRSObservation dependantObs2 = traverseDependantObservations(dependantObs, targetObsId);

                if (dependantObs2 != null) {
                    return dependantObs2;
                }
            }
        }

        return null;
    }
}
