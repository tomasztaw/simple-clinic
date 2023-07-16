package pl.taw.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.DoctorsDTO;
import pl.taw.business.dao.DoctorDAO;

import java.util.List;

@RestController
@RequestMapping(DoctorRestControllerSample.API_SAMPLE_DOCTORS)
@AllArgsConstructor
public class DoctorRestControllerSample {

    public static final String API_SAMPLE_DOCTORS = "/api/sample/doctors";
    public static final String LIST = "/list";
    public static final String DOCTOR_ID = "/{doctorId}";


    private DoctorDAO doctorDAO;

    // localhost:8080/clinic/api/doctors
    @GetMapping
    public List<DoctorDTO> getDoctorsSample() {
        return doctorDAO.findAll();
    }

    @GetMapping(value = LIST)
    public DoctorsDTO getDoctorsAsList() {
        return DoctorsDTO.of(doctorDAO.findAll());
    }

    // localhost:8080/clinic/api/doctors/1
    @GetMapping(value = DOCTOR_ID)
    public DoctorDTO doctorDetailsSample(
            @PathVariable("doctorId") Integer doctorId
    ) {
        return doctorDAO.findById(doctorId);
    }

    @GetMapping(value = DOCTOR_ID + "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DoctorDTO doctorDetailsSampleJson(@PathVariable("doctorId") Integer doctorId) {
        return doctorDAO.findById(doctorId);
    }

    @GetMapping(value = DOCTOR_ID + "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public DoctorDTO doctorDetailsSampleXml(@PathVariable("doctorId") Integer doctorId) {
        return doctorDAO.findById(doctorId);
    }


}
