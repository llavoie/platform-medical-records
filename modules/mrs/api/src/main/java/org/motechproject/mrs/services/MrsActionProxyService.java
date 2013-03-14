package org.motechproject.mrs.services;

import org.joda.time.DateTime;
import org.motechproject.mrs.exception.PatientNotFoundException;

public interface MrsActionProxyService {

    void createPatient(String patientId, String motechId, String facilityId, String personId);

    void updatePatient(String patientId, String motechId, String facilityId, String personId);

    void deceasePatient(String motechId, String conceptName, DateTime dateOfDeath, String comment) throws PatientNotFoundException;

    void createEncounter(String motechId, String facilityId, String userId, String providerId, DateTime encounterDate,
                         String encounterType, DateTime observationDate, String conceptName, String patientId, Object value);

    void createFacility(String name, String country, String region, String countyDistrict, String stateProvince);

    void createPerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                      DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate);

    void updatePerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                      DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate);

    void removePerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                      DateTime dateOfBirth, String birthDateEstimated, Integer age, String gender, String dead, DateTime deathDate);
}
