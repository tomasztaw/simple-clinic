package pl.taw.integration.support;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;
import pl.taw.api.controller.PatientController;
import pl.taw.api.controller.rest.PatientRestController;
import pl.taw.api.dto.PatientDTO;
import pl.taw.api.dto.PatientsDTO;

/**
 * Interfejs z curl-ami dla testów
 * w21-29
 */
public interface PatientsControllerTestSupport {

    RequestSpecification requestSpecification();
    RequestSpecification requestSpecificationNoAuthentication();

    default PatientsDTO listPatients() {
        return requestSpecification()
                .get(PatientRestController.API_PATIENTS)
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .extract()
                .as(PatientsDTO.class);
    }

    default PatientsDTO listPatientsNoAuto() {
        return requestSpecificationNoAuthentication()
                .get(PatientRestController.API_PATIENTS)
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .extract()
                .as(PatientsDTO.class);
    }

    default PatientDTO getPatient(final String path) {
        return requestSpecification()
                .get(path)
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .extract()
                .as(PatientDTO.class);
    }

    default PatientDTO getPatientById(final Integer patientId) {
        return requestSpecification()
                .get(PatientRestController.API_PATIENTS + PatientRestController.PATIENT_ID, patientId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .extract()
                .as(PatientDTO.class);
    }

    default ExtractableResponse<Response> savePatient(final PatientDTO patientDTO) {
        return requestSpecification()
                .body(patientDTO)
                .post(PatientRestController.API_PATIENTS)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .and()
                .extract();
    }

//    default void updatePatientByPet(final Integer patientId, final Long petId) {
//        String endpoint = PatientController.PATIENTS + PatientRestController.PATIENT_UPDATE_PET;
//        requestSpecification()
//                .patch(endpoint, patientId, petId)
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
}
