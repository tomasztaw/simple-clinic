package pl.taw.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import pl.taw.util.DtoFixtures;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PatientDTOTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void testValidPatientDTO() {
        // given
        String validPesel = "85061711333";
        String validPhone = "+48 123 456 789";
        String validEmail = "test@example.com";

        PatientDTO patientDTO = PatientDTO.builder()
                .name("Stefan")
                .surname("Kolorowy")
                .pesel(validPesel)
                .phone(validPhone)
                .email(validEmail)
                .build();

        // when
        Set<ConstraintViolation<PatientDTO>> violations = validator.validate(patientDTO);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidPatientDTO() {
        // given
        String invalidPesel = "123"; // Invalid length
        String invalidPhone = "+123456789"; // Invalid format
        String invalidEmail = "invalid_email"; // Invalid email format

        PatientDTO patientDTO = PatientDTO.builder()
                .name("Stefan")
                .surname("Kolorowy")
                .pesel(invalidPesel)
                .phone(invalidPhone)
                .email(invalidEmail)
                .build();

        // when
        Set<ConstraintViolation<PatientDTO>> violations = validator.validate(patientDTO);

        // then
        assertEquals(3, violations.size());
    }

    @Test
    public void testInvalidEmail() {
        // given
        String invalidEmail = "jakis#email.com";

        PatientDTO patientDTO = DtoFixtures.somePatient1().withEmail(invalidEmail);

        // when
        Set<ConstraintViolation<PatientDTO>> violations = validator.validate(patientDTO);

        // then
        assertFalse(violations.isEmpty());
    }

}