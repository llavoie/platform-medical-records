package org.motechproject.couch.mrs.model;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mrs.domain.MRSObservation;
import ch.lambdaj.Lambda;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
       @JsonSubTypes.Type(value = CouchObservation.class, name = "couchObservation")
})
public class CouchObservation<T> implements MRSObservation<T> {

    @JsonProperty("obsPatientId")
    private String patientId;
    private String observationId;
    @JsonProperty("obsDate")
    private DateTime date;
    private String conceptName;
    private T value;
    @JsonDeserialize(as = Set.class, contentAs = CouchObservation.class)
    @JsonSerialize(as = Set.class, contentAs = CouchObservation.class)
    private Set<CouchObservation> dependantObservations;

    public CouchObservation() {
        dependantObservations = new HashSet<CouchObservation>();
    }

    public CouchObservation(DateTime date, String conceptName, T value) {
        this();
        this.observationId = UUID.randomUUID().toString();
        this.date = date;
        this.conceptName = conceptName;
        this.value = value;
    }

    public CouchObservation(DateTime date, String conceptName, T value, String patientId) {
        this(date, conceptName, value);
        this.patientId = patientId;
    }

    public CouchObservation(String observationId, DateTime date, String conceptName, T value) {
        this(date, conceptName, value);
        this.observationId = observationId;
    }

    public CouchObservation(String observationId, DateTime date, String conceptName, T value, String patientId) {
        this(observationId, date, conceptName, value);
        this.patientId = patientId;
    }


    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getObservationId() {
        return observationId;
    }

    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    public DateTime getDate() {
        return DateUtil.setTimeZoneUTC(date);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public Set<? extends MRSObservation> getDependantObservations() {
        return dependantObservations;
    }

    @Override
    public void setDependantObservations(Set<MRSObservation> dependantObservations) {
        this.dependantObservations = new HashSet<CouchObservation>();
        if (dependantObservations != null) {
            Iterator<MRSObservation> iterator = dependantObservations.iterator();
            while (iterator.hasNext()) {
                MRSObservation obs = iterator.next();
                this.dependantObservations.add(convertObservationToCouchObservation(obs));
            }
        }
    }

    public void addDependantObservation(MRSObservation mrsObservation) {

        List<? extends MRSObservation> existingObservationList = Lambda.filter(having(on(CouchObservation.class).getConceptName(), is(equalTo(mrsObservation.getConceptName()))), dependantObservations);
        if (!existingObservationList.isEmpty()) {
            dependantObservations.remove(existingObservationList.get(0));
        }

        dependantObservations.add(convertObservationToCouchObservation(mrsObservation));
    }

    @Override
    public int hashCode() {
        int result = observationId != null ? observationId.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (conceptName != null ? conceptName.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (dependantObservations != null ? dependantObservations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MRSObservation{" +
                "id='" + observationId + '\'' +
                ", date=" + date +
                ", conceptName='" + conceptName + '\'' +
                ", value=" + value +
                '}';
    }

    public static CouchObservation convertObservationToCouchObservation(MRSObservation obs) {
        CouchObservation couchObs = new CouchObservation();
        couchObs.setConceptName(obs.getConceptName());
        couchObs.setDate(obs.getDate());
        couchObs.setObservationId(obs.getObservationId());
        couchObs.setPatientId(obs.getPatientId());
        couchObs.setValue(obs.getValue());

        Set<? extends MRSObservation> dependantObs = obs.getDependantObservations();

        if (dependantObs != null) {
            Iterator<? extends MRSObservation> iterator = dependantObs.iterator();
            while (iterator.hasNext()) {
                MRSObservation nextObs = iterator.next();
                nextObs = convertObservationToCouchObservation(nextObs);
                couchObs.addDependantObservation(nextObs);
            }
        }

        return couchObs;
    }
}
