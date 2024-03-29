package org.motechproject.openmrs.ws.resource.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.motechproject.openmrs.ws.resource.PatientResource;
import org.motechproject.openmrs.ws.resource.model.Identifier;
import org.motechproject.openmrs.ws.resource.model.IdentifierListResult;
import org.motechproject.openmrs.ws.resource.model.IdentifierType;
import org.motechproject.openmrs.ws.resource.model.IdentifierType.IdentifierTypeSerializer;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.Location.LocationSerializer;
import org.motechproject.openmrs.ws.resource.model.Patient;
import org.motechproject.openmrs.ws.resource.model.PatientIdentifierListResult;
import org.motechproject.openmrs.ws.resource.model.PatientListResult;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.resource.model.Person.PersonSerializer;
import org.motechproject.openmrs.ws.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientResourceImpl implements PatientResource {

    private RestClient restfulClient;
    private OpenMrsInstance openmrsInstance;

    private String motechIdTypeUuid;

    @Autowired
    public PatientResourceImpl(RestClient restClient, OpenMrsInstance instance) {
        this.restfulClient = restClient;
        this.openmrsInstance = instance;
    }

    @Override
    public Patient createPatient(Patient patient) throws HttpException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Person.class, new PersonSerializer())
                .registerTypeAdapter(IdentifierType.class, new IdentifierTypeSerializer())
                .registerTypeAdapter(Location.class, new LocationSerializer()).create();

        String requestJson = gson.toJson(patient);
        String responseJson = restfulClient.postForJson(openmrsInstance.toInstancePath("/patient"), requestJson);

        return (Patient) JsonUtils.readJson(responseJson, Patient.class);
    }

    @Override
    public PatientListResult queryForPatient(String motechId) throws HttpException {
        String responseJson = restfulClient.getJson(openmrsInstance.toInstancePathWithParams("/patient?q={motechId}",
                motechId));

        return (PatientListResult) JsonUtils.readJson(responseJson, PatientListResult.class);
    }

    @Override
    public Patient getPatientById(String patientId) throws HttpException {
        String responseJson = restfulClient.getJson(openmrsInstance.toInstancePathWithParams("/patient/{uuid}?v=full",
                patientId));

        return (Patient) JsonUtils.readJson(responseJson, Patient.class);
    }

    @Override
    public String getMotechPatientIdentifierUuid() throws HttpException {
        if (StringUtils.isNotEmpty(motechIdTypeUuid)) {
            return motechIdTypeUuid;
        }

        PatientIdentifierListResult result = getAllPatientIdentifierTypes();
        String motechPatientIdentifierTypeName = openmrsInstance.getMotechPatientIdentifierTypeName();
        for (IdentifierType type : result.getResults()) {
            if (motechPatientIdentifierTypeName.equals(type.getName())) {
                motechIdTypeUuid = type.getUuid();
                break;
            }
        }

        return motechIdTypeUuid;
    }

    @Override
    public void deletePatient(String patientUuid) throws HttpException {
        restfulClient.delete(openmrsInstance.toInstancePathWithParams("/patient/{uuid}?purge", patientUuid));
    }

    private PatientIdentifierListResult getAllPatientIdentifierTypes() throws HttpException {
        String responseJson = restfulClient.getJson(openmrsInstance.toInstancePath("/patientidentifiertype?v=full"));
        return (PatientIdentifierListResult) JsonUtils.readJson(responseJson, PatientIdentifierListResult.class);
    }

    @Override
    public void updatePatientMotechId(String patientUuid, String newMotechId) throws HttpException {
        Gson gson = new GsonBuilder().create();

        Identifier patientIdentifier = getPatientIdentifier(patientUuid);
        patientIdentifier.setIdentifier(newMotechId);
        String identifierUuid = patientIdentifier.getUuid();

        // setting uuid, type and location to null so they are not included in request.
        patientIdentifier.setUuid(null);
        patientIdentifier.setIdentifierType(null);
        patientIdentifier.setLocation(null);

        String requestJson = gson.toJson(patientIdentifier);
        restfulClient.postForJson(openmrsInstance.toInstancePathWithParams("/patient/{patientUuid}/identifier/{identifierUuid}",
                patientUuid, identifierUuid), requestJson);
    }

    private Identifier getPatientIdentifier(String patientUuid) throws HttpException {
        String responseJson =
                restfulClient.getJson(openmrsInstance.toInstancePathWithParams("/patient/{patientUuid}/identifier", patientUuid));
        IdentifierListResult identifierListResult =
                (IdentifierListResult) JsonUtils.readJson(responseJson, IdentifierListResult.class);
        return identifierListResult.getResults().get(0);
    }
}
