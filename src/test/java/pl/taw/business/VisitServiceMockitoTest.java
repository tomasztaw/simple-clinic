package pl.taw.business;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.dao.VisitDAO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceMockitoTest {

    @InjectMocks
    private VisitService visitService;

    @Mock
    private VisitDAO visitDAO;

    @Test
    public void testFindAllVisitForBoth() {
        // given
        Integer doctorId = 1;
        Integer patientId = 2;
        List<VisitDTO> visits = DtoFixtures.visits.stream()
                .map(visit -> visit.withDoctorId(doctorId).withPatientId(patientId)).toList();

        when(visitDAO.findAllForBoth(doctorId, patientId)).thenReturn(visits);

        // when
        List<VisitDTO> result = visitService.findAllVisitForBoth(doctorId, patientId);

        // then
        assertEquals(visits.size(), result.size());
        assertTrue(result.contains(visits.get(0)));
        assertTrue(result.contains(visits.get(1)));
        assertSame(visits, result);
        assertTrue(result.stream().allMatch(visit -> visit.getDoctorId().equals(doctorId) && visit.getPatientId().equals(patientId)));
        Assertions.assertThat(result).hasSize(2);

        verify(visitDAO, times(1)).findAllForBoth(doctorId, patientId);
        verify(visitDAO, only()).findAllForBoth(doctorId, patientId);
    }

    @Test
    public void testFindAllVisitByDoctor() {
        // given
        int doctorId = 5;
        List<VisitDTO> expectedVisits = DtoFixtures.visits.stream().map(visit -> visit.withDoctorId(doctorId)).toList();

        when(visitDAO.findAllByDoctor(doctorId)).thenReturn(expectedVisits);

        // when
        List<VisitDTO> actualVisits = visitService.findAllVisitByDoctor(doctorId);

        // then
        assertEquals(expectedVisits, actualVisits);
        assertEquals(expectedVisits.size(), actualVisits.size());
        assertTrue(actualVisits.stream().allMatch(visit -> visit.getDoctorId().equals(doctorId)));
        Assertions.assertThat(actualVisits).hasSize(2);

        verify(visitDAO, times(1)).findAllByDoctor(doctorId);
        verify(visitDAO, only()).findAllByDoctor(doctorId);
    }

    @Test
    public void testFindAllVisitByPatient() {
        // given
        PatientEntity patient = EntityFixtures.somePatient1();
        List<VisitDTO> expectedVisits = DtoFixtures.visits.stream().map(visit -> visit.withPatientId(patient.getPatientId())).toList();

        when(visitDAO.findAllByPatient(patient.getPatientId())).thenReturn(expectedVisits);

        // when
        List<VisitDTO> actualVisits = visitService.findAllVisitByPatient(patient.getPatientId());

        // then
        assertTrue(actualVisits.stream().allMatch(visit -> visit.getPatientId().equals(patient.getPatientId())));
        assertEquals(expectedVisits, actualVisits);
        Assertions.assertThat(actualVisits).hasSize(2);

        verify(visitDAO, times(1)).findAllByPatient(patient.getPatientId());
        verify(visitDAO, only()).findAllByPatient(patient.getPatientId());
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
        assertEquals(5, result.size());
        assertTrue(result.stream().allMatch(visit -> visit.getDoctorId().equals(doctor.getDoctorId())
                && visit.getPatientId().equals(patient.getPatientId())));

        verify(visitDAO, times(1)).findAllForBoth(doctor.getDoctorId(), patient.getPatientId());
        verify(visitDAO, only()).findAllForBoth(doctor.getDoctorId(), patient.getPatientId());
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
        verify(visitDAO, only()).findAllForBoth(doctor.getDoctorId(), patient.getPatientId());
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
        verify(visitDAO, only()).findAllForBoth(doctor.getDoctorId(), patient.getPatientId());
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
        assertEquals(visits, result);
        assertTrue(result.stream().allMatch(visit -> visit.getDoctorId().equals(doctorId)));

        verify(visitDAO, times(1)).findAllByDoctor(doctorId);
        verify(visitDAO, only()).findAllByDoctor(doctorId);
    }

}