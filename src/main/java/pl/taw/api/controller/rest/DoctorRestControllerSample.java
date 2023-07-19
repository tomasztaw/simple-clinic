package pl.taw.api.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.DoctorsDTO;
import pl.taw.business.dao.DoctorDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(DoctorRestControllerSample.API_SAMPLE_DOCTORS)
@AllArgsConstructor
public class DoctorRestControllerSample {

    public static final String API_SAMPLE_DOCTORS = "/api/sample/doctors";
    public static final String LIST = "/list";
    public static final String DOCTOR_ID = "/{doctorId}";
    public static final String DOCTOR_UPDATE_TITLE = "/{doctorId}/title";
    public static final String DOCTOR_ID_RESULT = "/%s";


    private DoctorDAO doctorDAO;

    // ################### GET ###################

    // localhost:8080/clinic/api/doctors
    @GetMapping
    public List<DoctorDTO> getDoctorsSample() {
        return doctorDAO.findAll();
    }

    /**
     * Zwracany obiekt jako listę zamiast listy jak wyżej
     */
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

    /**
     * Można połączyć zwracanie json-a i xml w jednej metodzie
     * Musimy dodać w zapytaniu nagłówek 'curl -i -H "Accept: application/json"....'
     */
    @GetMapping(value = DOCTOR_ID,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public DoctorDTO doctorDetailsSampleBoth(@PathVariable("doctorId") Integer doctorId) {
        return doctorDAO.findById(doctorId);
    }

    // #################### POST ####################
    /**
     * Obsługiwany jest endpoint główny, do którego przesyłamy dane (@RequestBody doctorDTO) w żądaniu.
     * Ciało żądania zostanie przekształcone na obiekt DoctorDTO z uwzględnioną walidacją.
     * Możemy użyć adnotacji @Transactional, ale można jej użyć w serwisie obsługującym ten rest.
     * Zwracany jest status 201 ze ścieżką dla nowego lekarza (.../clinic/api/sample/doctors/[?18?]
     * Pamiętaj, że POST nie jest idempotentny. Za każdym razem będzie tworzony nowy zasób.
     */
    @PostMapping("/nie-wiem-czy-to-prawidlowo-bez-sciezki")
    @Transactional
    public ResponseEntity<DoctorDTO> addDoctorSample(
            @Valid @RequestBody DoctorDTO doctorDTO
    ) {
        DoctorEntity doctorEntity = DoctorEntity.builder()
                .name(doctorDTO.getName())
                .surname(doctorDTO.getSurname())
                .title(doctorDTO.getTitle())
                .phone(doctorDTO.getPhone())
                .email(doctorDTO.getEmail())
                .build();

        DoctorEntity created = doctorDAO.saveAndReturn(doctorEntity);

        return ResponseEntity
                .created(URI.create(DOCTOR_ID.concat(DOCTOR_ID_RESULT).formatted(created.getDoctorId())))
                .build();
    }

    // #################### PUT ####################

    /**
     * Aktualizacja zasobu, w założeniu powinna być operacją idempotentną.
     * Obsługiwana na głównym endpoint-ce rest api.
     * W argumencie podajemy id lekarza i ciało z obiektem do aktualizacji
     * Nie zwracany jest żaden obiekt, tylko status 200
     */
    @PutMapping(DOCTOR_ID)
    @Transactional
    public ResponseEntity<?> updateDoctorSample(
            @PathVariable("doctorId") Integer doctorId,
            @Valid @RequestBody DoctorDTO doctorDTO
    ) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);

        existingDoctor.setName(doctorDTO.getName());
        existingDoctor.setSurname(doctorDTO.getSurname());
        existingDoctor.setTitle(doctorDTO.getTitle());
        existingDoctor.setPhone(doctorDTO.getPhone());
        existingDoctor.setEmail(doctorDTO.getEmail());

        doctorDAO.save(existingDoctor);

        return ResponseEntity.ok().build();
    }

    // #################### DELETE ####################
    /**
     * Operacja idempotentna, wielokrotnie wykonana nie zmieni stanu zasobu, co najwyżej zwrócony będzie status 404.
     * Najpierw sprawdzamy, czy jest taki lekarz w bazie, a następnie kasujemy i zwracamy noContent(204), lub przy braku zasobu zwracamy notFound(404)
     */
    @DeleteMapping(DOCTOR_ID)
    public ResponseEntity<?> deleteDoctorSample(
            @PathVariable("doctorId") Integer doctorId
    ) {
        Optional<DoctorDTO> existingDoctor = Optional.ofNullable(doctorDAO.findById(doctorId));
        if (existingDoctor.isPresent()) {
            doctorDAO.deleteById(existingDoctor.get().getDoctorId());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(DOCTOR_ID + "/cokolwiek")
    public ResponseEntity<?> deleteDoctorSample2(
            @PathVariable("doctorId") Integer doctorId
    ) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);

        doctorDAO.delete(existingDoctor);

        return ResponseEntity.noContent().build();
    }

    // #################### PATCH ####################
    /**
     * Jeżeli tylko zmieniamy zasób, to operacja jest idempotentna. Jeżeli dodajemy jakiś zasób, to nie jest.
     * W argumencie podajemy parametr z nowym tytułem lekarza. @RequestParam dodajemy w ścieżce po znaku zapytanie (?)
     * 'curl -X PATCH htt.../api/samples/doctors/5/title?newTitle=Kardiolog'
     */
    @PatchMapping(DOCTOR_UPDATE_TITLE)
    public ResponseEntity<?> updateDoctorTitleSample(
            @PathVariable("doctorId") Integer doctorId,
            @RequestParam(required = true) String newTitle
    ) {
        DoctorEntity existingDoctor = doctorDAO.findEntityById(doctorId);

        existingDoctor.setTitle(newTitle);

        doctorDAO.save(existingDoctor);

        return ResponseEntity.ok().build();
    }

    // #################### ResponseEntity<T> ####################
    /**
     * Nie za każdym razem musimy używać tej klasy. Robimy to, kiedy potrzebujemy więcej możliwości niż zwrócenie prostego obiektu.
     * Klasa ta służy do reprezentacji całej odpowiedzi http. Możemy użyć ciała, nagłówka i status odpowiedzi http.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<DoctorDTO> addDoctorSampleExample(
            @Valid @RequestBody DoctorDTO doctorDTO
    ) {
        DoctorEntity doctorEntity = DoctorEntity.builder()
                .name(doctorDTO.getName())
                .surname(doctorDTO.getSurname())
                .title(doctorDTO.getTitle())
                .phone(doctorDTO.getPhone())
                .email(doctorDTO.getEmail())
                .build();

        DoctorEntity created = doctorDAO.saveAndReturn(doctorEntity);

        // dodajemy nagłówek ręcznie
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", DOCTOR_ID.concat(DOCTOR_ID_RESULT).formatted(created.getDoctorId()));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // #################### Nagłówki ####################

    @GetMapping(value = "test-header")
    public ResponseEntity<?> testHeader(
            @RequestHeader(value = HttpHeaders.ACCEPT) MediaType accept,
            @RequestHeader(value = "httpStatus", required = true) int httpStatus
            ) {
        return ResponseEntity
                .status(httpStatus)
                .body("Accepted: " + accept);
    }




}
