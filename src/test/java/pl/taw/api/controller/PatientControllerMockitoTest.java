package pl.taw.api.controller;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.jpa.PatientJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.PatientMapper;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerMockitoTest {

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private PatientJpaRepository patientJpaRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientController patientController;


//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }


//    @Ignore
//    @Test
//    void shouldReturnAllPatients() {
//        // given
//        // tworzymy listy encji/dto z klas pomocniczych:
//        List<PatientEntity> allPatients = List.of(EntityFixtures.somePatient1(),
//                EntityFixtures.somePatient2(), EntityFixtures.somePatient3());
//        System.out.println("allPatients = " + allPatients);
//
//        List<PatientDTO> expectedDTOs = Arrays.asList(
//                DtoFixtures.somePatient1(), DtoFixtures.somePatient2());
//        System.out.println("expectedDTOs = " + expectedDTOs);
//
//        when(patientJpaRepository.findAll()).thenReturn(allPatients);
//        when(patientMapper.mapFromEntity(allPatients.get(0))).thenReturn(DtoFixtures.somePatient1());
//
//        // when
//        var result = patientController.patientsList();
//        System.out.println("result = " + result);
//
//        // then
//        assertThat(result.size()).isEqualTo(allPatients.size());
//        verify(patientJpaRepository, times(1)).findAll();
//        verify(patientMapper, times(1)).mapFromEntity(allPatients.get(0));
//    }

    @Test
    void thatSavingPatientWorksCorrectly() {
        // given
        when(patientJpaRepository.save(any(PatientEntity.class)))
                .thenReturn(EntityFixtures.somePatient1().withPatientId(123));

        // when
        ResponseEntity<?> result = patientController.addRequestPatient(DtoFixtures.somePatient1());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testAddPatient() {
        // given
        PatientDTO patientDTO = DtoFixtures.somePatient2();
        PatientEntity savedPatientEntity = EntityFixtures.somePatient3();
        when(patientJpaRepository.save(any(PatientEntity.class))).thenReturn(savedPatientEntity);

        // when
        ResponseEntity<PatientDTO> response = patientController.addRequestPatient(patientDTO);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(patientJpaRepository, times(1)).save(any(PatientEntity.class));
    }

    @Test
    public void testUpdatePatient_Success() {
        // given
        Integer patientId = 1;
        PatientDTO patientDTO = DtoFixtures.somePatient2();

        PatientEntity existingPatient = EntityFixtures.somePatient1();
        existingPatient.setName(patientDTO.getName());
        existingPatient.setSurname(patientDTO.getSurname());
        existingPatient.setPesel(patientDTO.getPesel());
        existingPatient.setPhone(patientDTO.getPhone());
        existingPatient.setEmail(patientDTO.getEmail());

        when(patientJpaRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));

        // when
        ResponseEntity<?> response = patientController.updateRequestPatient(patientId, patientDTO);

        // then
        verify(patientJpaRepository).findById(patientId);
        verify(patientJpaRepository).save(existingPatient);
        assertThat(ResponseEntity.ok().build()).isEqualTo(response);
        assertThat(patientDTO.getName()).isEqualTo(existingPatient.getName());
        assertThat(patientDTO.getSurname()).isEqualTo(existingPatient.getSurname());
        assertThat(patientDTO.getPesel()).isEqualTo(existingPatient.getPesel());
        assertThat(patientDTO.getPhone()).isEqualTo(existingPatient.getPhone());
        assertThat(patientDTO.getEmail()).isEqualTo(existingPatient.getEmail());
    }

    @Test
    void shouldDeletePatientCorrectly() {
        // given
        Integer patientId = 1;
        var patientOpt = patientJpaRepository.findById(patientId);
        PatientEntity existingPatient = EntityFixtures.somePatient3();
        when(patientJpaRepository.findById(patientId)).thenReturn(Optional.ofNullable(existingPatient));

        // when
        ResponseEntity<?> response = patientController.deletePatient(patientId);

        // then
        assertThat(response).isEqualTo(ResponseEntity.ok().build());
    }

    @Test
    public void testDeletePatient_ExistingPatient() {
        // Mockowanie zwracanej wartości przez repository
        when(patientJpaRepository.findById(anyInt())).thenReturn(Optional.of(EntityFixtures.somePatient1()));

        // Wywołanie metody kontrolera
        ResponseEntity<?> response = patientController.deletePatient(1);

        // Sprawdzenie, czy metoda deleteById została wywołana z prawidłowym argumentem
        verify(patientJpaRepository).deleteById(1);

        // Sprawdzenie, czy odpowiedź ma status 200 (OK)
        assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
    }

    @Test
    public void testDeletePatient_NonExistingPatient() {
        // Mockowanie zwracanej wartości przez repository
        when(patientJpaRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Wywołanie metody kontrolera
        ResponseEntity<?> response = patientController.deletePatient(1);

        // Sprawdzenie, czy metoda deleteById nie została wywołana
        verify(patientJpaRepository, never()).deleteById(anyInt());

        // Sprawdzenie, czy odpowiedź ma status 404 (Not Found)
        assertThat(HttpStatus.NOT_FOUND).isEqualTo(response.getStatusCode());
    }

}