package org.motechproject.mrs.helper;


import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.model.MRSAttributeDto;
import org.motechproject.mrs.model.MRSFacilityDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.model.MRSPersonDto;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientHelperTest {
    @Test
    public void shouldBuildPatientDtoModel() {
        final String patientID = "123";
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String preferred = "preferred";
        final DateTime birthDate = new DateTime();
        final DateTime deathDate = new DateTime();
        final Boolean dead = true;
        final String gender = "male";
        final String address = "Long street 5/10";
        Boolean birthDateEstimated = true;

        MRSPersonDto personDto = new MRSPersonDto();
        personDto.setPersonId(patientID);
        personDto.setFirstName(first);
        personDto.setLastName(last);
        personDto.setMiddleName(middle);
        personDto.setPreferredName(preferred);
        personDto.setDateOfBirth(birthDate);
        personDto.setBirthDateEstimated(birthDateEstimated);
        personDto.setAddress(address);
        personDto.setGender(gender);
        personDto.setDead(dead);
        personDto.setDeathDate(deathDate);

        final MRSFacilityDto facility = new MRSFacilityDto();
        facility.setFacilityId("1000");

        final String motechId = "1000";
        final String patientId = "125";

        MRSPatientDto patientDto = new MRSPatientDto();
        patientDto.setFacility(facility);
        patientDto.setPerson(personDto);
        patientDto.setMotechId(motechId);
        patientDto.setPatientId(patientId);

        MRSPatientDto returnedPatient = MRSDtoHelper.createPatientDto(patientDto);

        assertThat(returnedPatient.getMotechId(), is(equalTo(motechId)));
        assertThat(returnedPatient.getPatientId(), is(equalTo(patientId)));
        assertThat(returnedPatient.getPerson().getGender(), is(equalTo(gender)));
        assertThat(returnedPatient.getPerson().getDateOfBirth(), is(equalTo(birthDate)));
        assertThat(returnedPatient.getPerson().getBirthDateEstimated(), is(equalTo(birthDateEstimated)));
        assertThat(returnedPatient.getPerson().getAddress(), is(equalTo(address)));
        assertThat(returnedPatient.getPerson().getAttributes().size(), is(0));
        assertThat(returnedPatient.getPerson().getDeathDate(), is(equalTo(deathDate)));
        assertThat(returnedPatient.getPerson().isDead(), is(equalTo(dead)));
    }

    @Test
    public void shouldBuildAttributeListModel() {
        MRSAttributeDto attributeDto1 = new MRSAttributeDto();
        attributeDto1.setName("name1");
        attributeDto1.setValue("value1");

        MRSAttributeDto attributeDto2 = new MRSAttributeDto();
        attributeDto2.setName("name2");
        attributeDto2.setValue("value2");

        final List<MRSAttributeDto> patientAttributes = new ArrayList<>();
        patientAttributes.add(attributeDto1);
        patientAttributes.add(attributeDto2);

        List<MRSAttribute> returnedAttributeList = MRSDtoHelper.getAttributesList(patientAttributes);

        assertThat(returnedAttributeList.get(0).getName(), is(equalTo(attributeDto1.getName())));
        assertThat(returnedAttributeList.get(1).getValue(), is(equalTo(attributeDto2.getValue())));
        assertThat(returnedAttributeList.size(), is(2));
    }

}



