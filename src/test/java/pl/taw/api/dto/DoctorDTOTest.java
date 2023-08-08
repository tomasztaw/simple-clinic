package pl.taw.api.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import pl.taw.util.DtoFixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DoctorDTOTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testAsMap() {
        DoctorDTO doctorDTO = DoctorDTO.builder()
                .doctorId(1)
                .name("Jan")
                .surname("Kowalski")
                .phone("+48 123 456 789")
                .email("jan.kowalski@eclinic.pl")
                .build();

        Map<String, String> resultMap = doctorDTO.asMap();

        assertEquals("1", resultMap.get("doctorId"));
        assertEquals("Jan", resultMap.get("name"));
        assertEquals("Kowalski", resultMap.get("surname"));
        assertEquals("+48 123 456 789", resultMap.get("phone"));
        assertEquals("jan.kowalski@eclinic.pl", resultMap.get("email"));
    }

    @Test
    public void testValidation_ValidDoctor() {
        DoctorDTO doctorDTO = DoctorDTO.builder()
                .name("Jan")
                .surname("Kowalski")
                .phone("+48 123 456 789")
                .email("jan.kowalski@eclinic.pl")
                .build();

        assertTrue(validator.validate(doctorDTO).isEmpty());
    }

    @Test
    public void testValidation_InvalidPhone_toShort() {
        DoctorDTO doctorDTO = DoctorDTO.builder()
                .name("Jan")
                .surname("Kowalski")
                .phone("+48 123")
                .email("jan.kowalski@eclinic.pl")
                .build();

        assertFalse(validator.validate(doctorDTO).isEmpty());
    }

    @Test
    public void testValidation_InvalidPhone_toLong() {
        DoctorDTO doctorDTO = DoctorDTO.builder()
                .name("Jan")
                .surname("Kowalski")
                .phone("+48 123 333 555 78")
                .email("jan.kowalski@eclinic.pl")
                .build();

        assertFalse(validator.validate(doctorDTO).isEmpty());
    }

    @Test
    public void testValidation_InvalidEmail() {
        DoctorDTO doctorDTO = DoctorDTO.builder()
                .name("Jan")
                .surname("Kowalski")
                .phone("+48 123 333 555 78")
                .email("jan.kowalski#eclinic.pl")
                .build();

        assertFalse(validator.validate(doctorDTO).isEmpty());
    }

    @Test
    public void testValidation_InvalidEmailFromInvalidDoctor() {
        DoctorDTO doctorDTO = DtoFixtures.invalidDoctor();

        assertFalse(validator.validate(doctorDTO).isEmpty());
    }

    @Test
    public void testValidation_InvalidEmailWithMethod() {
        DoctorDTO doctorDTO = DtoFixtures.someDoctor3().withEmail("zly.email.com");

        assertFalse(validator.validate(doctorDTO).isEmpty());
    }

    @Test
    public void testVisitsField() {
        // given
        Integer doctorId = 2;
        List<VisitDTO> visitsList = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctorId)).toList();

        // when
        DoctorDTO doctorDTO = DoctorDTO.builder()
                .doctorId(doctorId)
                .name("Jan")
                .surname("Kowalski")
                .phone("+48 123 456 789")
                .email("jan.kowalski@eclinic.pl")
                .visits(visitsList)
                .build();

        // then
        assertEquals(visitsList, doctorDTO.getVisits());
    }

}