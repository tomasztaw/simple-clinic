package pl.taw.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.util.EntityFixtures;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @InjectMocks
    private VisitService visitService;

    @Mock
    private VisitDAO visitDAO;

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
        VisitDTO visit2 = VisitDTO.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .note("cokolwiek")
                .build();
        VisitDTO visit3 = VisitDTO.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .note("cokolwiek")
                .build();

        List<VisitDTO> visits = Arrays.asList(visit1, visit2, visit3);

        Mockito.when(visitDAO.findAllForBoth(doctorId, patientId)).thenReturn(visits);

        // when
        List<VisitDTO> result = visitService.findAllVisitForBoth(doctorId, patientId);

        // then
        Mockito.verify(visitDAO, Mockito.times(1)).findAllForBoth(doctorId, patientId);
        assertEquals(3, result.size());
        assertTrue(result.contains(visit1));
        assertTrue(result.contains(visit2));
        assertSame(visits, result);
    }

    @Test
    public void testFindAllVisitByDoctor() {
        // given
        int doctorId = 5;
        VisitDTO visit10 = VisitDTO.builder().visitId(10).doctorId(doctorId).patientId(8).build();
        VisitDTO visit12 = VisitDTO.builder().visitId(12).doctorId(doctorId).patientId(12).build();
        VisitDTO visit18 = VisitDTO.builder().visitId(18).doctorId(doctorId).patientId(15).build();

        List<VisitDTO> expectedVisits = Arrays.asList(visit10, visit12, visit18);

        when(visitDAO.findAllByDoctor(doctorId)).thenReturn(expectedVisits);

        // when
        List<VisitDTO> actualVisits = visitService.findAllVisitByDoctor(doctorId);

        // then
        verify(visitDAO, times(1)).findAllByDoctor(doctorId);
        assertEquals(expectedVisits, actualVisits);
        assertEquals(3, actualVisits.size());
        assertTrue(actualVisits.stream().allMatch(visit -> visit.getDoctorId().equals(doctorId)));
    }

    @Test
    public void testFindAllVisitByPatient() {
        // given
        PatientEntity patient = EntityFixtures.somePatient1();
        List<VisitDTO> expectedVisits = Arrays.asList(
                VisitDTO.builder().visitId(2).doctorId(10).patientId(patient.getPatientId()).build(),
                VisitDTO.builder().visitId(20).doctorId(2).patientId(patient.getPatientId()).build(),
                VisitDTO.builder().visitId(38).doctorId(7).patientId(patient.getPatientId()).build());
        when(visitDAO.findAllByPatient(patient.getPatientId())).thenReturn(expectedVisits);

        // when
        List<VisitDTO> actualVisits = visitService.findAllVisitByPatient(patient.getPatientId());

        // then
        verify(visitDAO, times(1)).findAllByPatient(patient.getPatientId());
        assertTrue(actualVisits.stream().allMatch(visit -> visit.getPatientId().equals(patient.getPatientId())));
        assertEquals(expectedVisits, actualVisits);

    }

    @Test
    public void testFindAllVisitForBothOld() {
        // given
        PatientEntity patient = EntityFixtures.somePatient1();
        DoctorEntity doctor = EntityFixtures.someDoctor1();
        List<VisitDTO> expectedVisits = Arrays.asList(
                VisitDTO.builder().visitId(2).doctorId(doctor.getDoctorId()).patientId(patient.getPatientId()).build(),
                VisitDTO.builder().visitId(20).doctorId(doctor.getDoctorId()).patientId(patient.getPatientId()).build(),
                VisitDTO.builder().visitId(25).doctorId(doctor.getDoctorId()).patientId(patient.getPatientId()).build(),
                VisitDTO.builder().visitId(32).doctorId(doctor.getDoctorId()).patientId(patient.getPatientId()).build(),
                VisitDTO.builder().visitId(38).doctorId(doctor.getDoctorId()).patientId(patient.getPatientId()).build());
        when(visitDAO.findAllForBoth(doctor.getDoctorId(), patient.getPatientId())).thenReturn(expectedVisits);

        // when
        List<VisitDTO> result = visitService.findAllVisitForBoth(doctor.getDoctorId(), patient.getPatientId());

        // then
        verify(visitDAO, times(1)).findAllForBoth(doctor.getDoctorId(), patient.getPatientId());
        assertEquals(5, result.size());
        assertTrue(result.stream().allMatch(visit -> visit.getDoctorId().equals(doctor.getDoctorId())
                && visit.getPatientId().equals(patient.getPatientId())));
    }

    @Test
    void testHasPatientSeenThisDoctor_returnTrue() {
        // given
        DoctorEntity doctor = EntityFixtures.someDoctor2();
        PatientEntity patient = EntityFixtures.somePatient2();

        List<VisitDTO> expectedVisits = Arrays.asList(
                VisitDTO.builder().visitId(2).doctorId(doctor.getDoctorId()).patientId(patient.getPatientId()).build(),
                VisitDTO.builder().visitId(38).doctorId(doctor.getDoctorId()).patientId(patient.getPatientId()).build());
        when(visitDAO.findAllForBoth(doctor.getDoctorId(), patient.getPatientId())).thenReturn(expectedVisits);

        // when
        boolean result = visitService.hasPatientSeenThisDoctor(doctor.getDoctorId(), patient.getPatientId());

        // then
        assertTrue(result);
        verify(visitDAO, times(1)).findAllForBoth(doctor.getDoctorId(), patient.getPatientId());
    }

    @Test
    void testHasPatientSeenThisDoctor_returnFalse() {
        // given
        DoctorEntity doctor = EntityFixtures.someDoctor3();
        PatientEntity patient = EntityFixtures.somePatient2();
        List<VisitDTO> visits = Collections.emptyList();

        when(visitDAO.findAllForBoth(doctor.getDoctorId(), patient.getPatientId())).thenReturn(visits);

        // when
        boolean result = visitService.hasPatientSeenThisDoctor(doctor.getDoctorId(), patient.getPatientId());

        // then
        assertFalse(result);
        verify(visitDAO, times(1)).findAllForBoth(doctor.getDoctorId(), patient.getPatientId());
    }

    @Test
    void testFindAllVisitByDoctorArgumentsMatchers() {
        // given
        int doctorId = 18;
        VisitDTO visit10 = VisitDTO.builder().visitId(10).doctorId(doctorId).patientId(8).build();
        VisitDTO visit12 = VisitDTO.builder().visitId(12).doctorId(doctorId).patientId(12).build();
        List<VisitDTO> visits = List.of(visit10, visit12);
        when(visitDAO.findAllByDoctor(ArgumentMatchers.anyInt())) // jeżeli do tej metody wpadnie jakikolwiek argument danego typu
                .thenReturn(visits);

        // when
        List<VisitDTO> result = visitService.findAllVisitByDoctor(doctorId);


        // then
        verify(visitDAO, times(1)).findAllByDoctor(doctorId);
        assertEquals(visits, result);
        assertTrue(result.stream().allMatch(visit -> visit.getDoctorId().equals(doctorId)));
    }

}