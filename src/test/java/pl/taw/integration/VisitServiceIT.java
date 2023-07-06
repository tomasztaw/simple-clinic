package pl.taw.integration;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.VisitDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class VisitServiceIT {

    @Autowired
    private VisitService visitService;

    @Mock
    private VisitDAO visitDAO;

    @Test
    public void testHasPatientSeenThisDoctor_PatientHasSeenDoctor_ReturnsTrue() {
        int doctorId = 1;
        int patientId = 1;
        List<VisitDTO> visits = new ArrayList<>();
        // Dodaj wizytę dla danego doktora i pacjenta
        VisitDTO visit = VisitDTO.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .build();
        visits.add(visit);

        when(visitDAO.findAllForBoth(doctorId, patientId)).thenReturn(visits);

        boolean hasSeenDoctor = visitService.hasPatientSeenThisDoctor(doctorId, patientId);

        assertTrue(hasSeenDoctor);
    }

    @Test
    public void testHasPatientSeenThisDoctor_PatientHasNotSeenDoctor_ReturnsFalse() {
        int doctorId = 3;
        int patientId = 1;
        List<VisitDTO> visits = Collections.emptyList();

        when(visitDAO.findAllForBoth(doctorId, patientId)).thenReturn(visits);

        boolean hasSeenDoctor = visitService.hasPatientSeenThisDoctor(doctorId, patientId);

        assertFalse(hasSeenDoctor);
    }

//    @Test
//    public void testFindAllVisitByDoctor() {
//        int doctorId = 1;
//        List<VisitDTO> expectedVisits = new ArrayList<>();
//        VisitDTO visit1 = VisitDTO.builder().doctorId(doctorId).build();
//        VisitDTO visit2 = VisitDTO.builder().doctorId(doctorId).build();
//        VisitDTO visit3 = VisitDTO.builder().doctorId(doctorId).build();
//
//        when(visitDAO.findAllByDoctor(doctorId)).thenReturn(expectedVisits);
//
//        List<VisitDTO> actualVisits = visitService.findAllVisitByDoctor(doctorId);
//
//        assertEquals(expectedVisits, actualVisits);
//        assertTrue(expectedVisits.contains(visit2));
//    }
//
//    @Test
//    public void testFindAllVisitByPatient() {
//        int patientId = 1;
//        List<VisitDTO> expectedVisits = new ArrayList<>();
//        // Dodaj oczekiwane wizyty do listy expectedVisits
//
//        when(visitDAO.findAllByPatient(patientId)).thenReturn(expectedVisits);
//
//        List<VisitDTO> actualVisits = visitService.findAllVisitByPatient(patientId);
//
//        assertEquals(expectedVisits, actualVisits);
//    }
//
//    @Test
//    public void testFindAllVisitForBoth() {
//        int doctorId = 1;
//        int patientId = 1;
//        List<VisitDTO> expectedVisits = new ArrayList<>();
//        // Dodaj oczekiwane wizyty do listy expectedVisits
//
//        when(visitDAO.findAllForBoth(doctorId, patientId)).thenReturn(expectedVisits);
//
//        List<VisitDTO> actualVisits = visitService.findAllVisitForBoth(doctorId, patientId);
//
//        assertEquals(expectedVisits, actualVisits);
//    }
}