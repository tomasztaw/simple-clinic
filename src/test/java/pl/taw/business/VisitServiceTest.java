package pl.taw.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.VisitRepository;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;
import pl.taw.infrastructure.database.repository.mapper.VisitMapperImpl;
import pl.taw.util.EntityFixtures;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @InjectMocks
    private VisitService visitService;

    @Mock
    private VisitRepository visitRepository;

    private VisitMapper visitMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        visitMapper = new VisitMapperImpl();
    }

    @Test
    public void testFindAllVisitForBoth() {
        // given
        Integer doctorId = 1;
        Integer patientId = 2;

        VisitDTO visit1 = VisitDTO.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .note("cokolwiek")
                .build();
//                new VisitDTO(1, doctorId, patientId);
        VisitDTO visit2 = VisitDTO.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .note("cokolwiek")
                .build();
//                new VisitDTO(2, doctorId, patientId);
        VisitDTO visit3 = VisitDTO.builder()
                .doctorId(5)
                .patientId(patientId)
                .note("cokolwiek")
                .build();
//                new VisitDTO(3, 3, patientId);
        List<VisitDTO> visits = Arrays.asList(visit1, visit2, visit3);

        when(visitRepository.findAll()).thenReturn(visits);

        // when
        List<VisitDTO> result = visitService.findAllVisitForBoth(doctorId, patientId);

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(visit1));
        assertTrue(result.contains(visit2));
        assertFalse(result.contains(visit3));
    }

    @Test
    public void testFindAllVisitByDoctor() {
        // given
        DoctorEntity doctor = EntityFixtures.someDoctor1();
        List<VisitDTO> expectedVisits = EntityFixtures.someVisitList.stream()
                .map(visitMapper::mapFromEntity)
                .toList();
        when(visitRepository.findAllByDoctor(doctor.getDoctorId())).thenReturn(expectedVisits);

        // when
        List<VisitDTO> actualVisits = visitService.findAllVisitByDoctor(doctor.getDoctorId());

        // then
        assertEquals(expectedVisits, actualVisits);
    }

    @Test
    public void testFindAllVisitByPatient() {
        // given
        PatientEntity patient = EntityFixtures.somePatient1();
        List<VisitDTO> expectedVisits = EntityFixtures.someVisitList.stream()
                .map(visitMapper::mapFromEntity)
                .toList();
        when(visitRepository.findAllByPatient(patient.getPatientId())).thenReturn(expectedVisits);

        // when
        List<VisitDTO> actualVisits = visitService.findAllVisitByPatient(patient.getPatientId());

        // then
        assertEquals(expectedVisits, actualVisits);
    }

    @Test
    public void testFindAllVisitForBothOld() {
        // given
        PatientEntity patient = EntityFixtures.somePatient1();
        DoctorEntity doctor = EntityFixtures.someDoctor1();
        List<VisitDTO> visits = EntityFixtures.someVisitList.stream()
                .map(visitMapper::mapFromEntity)
                .toList();

        // when
        when(visitRepository.findAll()).thenReturn(visits);
        List<VisitDTO> result = visitService.findAllVisitForBoth(doctor.getDoctorId(), patient.getPatientId());

        // then
        assertEquals(1, result.size());
        verify(visitRepository, times(1)).findAll();
    }

    @Test
    void testHasPatientSeenThisDoctor_returnTrue() {
        // given
        DoctorEntity doctor = EntityFixtures.someDoctor2();
        PatientEntity patient = EntityFixtures.somePatient2();
        List<VisitDTO> visits = EntityFixtures.someVisitList.stream()
                .map(visitMapper::mapFromEntity)
                .toList();

        when(visitRepository.findAll()).thenReturn(visits);

        // when
        boolean result = visitService.hasPatientSeenThisDoctor(doctor.getDoctorId(), patient.getPatientId());

        // then
        assertTrue(result);
        verify(visitRepository, times(1)).findAll();
    }

    @Test
    void testHasPatientSeenThisDoctor_returnFalse() {
        // given
        DoctorEntity doctor = EntityFixtures.someDoctor3();
        PatientEntity patient = EntityFixtures.somePatient2();
        List<VisitDTO> visits = EntityFixtures.someVisitList.stream()
                .map(visitMapper::mapFromEntity)
                .toList();

        when(visitRepository.findAll()).thenReturn(visits);

        // when
        boolean result = visitService.hasPatientSeenThisDoctor(doctor.getDoctorId(), patient.getPatientId());

        // then
        assertFalse(result);
        verify(visitRepository, times(1)).findAll();
    }

}