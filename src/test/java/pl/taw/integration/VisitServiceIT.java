package pl.taw.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.taw.api.dto.VisitDTO;
import pl.taw.business.VisitService;
import pl.taw.business.dao.VisitDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
//@ContextConfiguration(classes = {TestBeanConfiguration.class})
public class VisitServiceIT {

    @Autowired
    private VisitService visitService;

//    @Mock
    @MockBean // TODO - czy to jest ten lepszy sposób?
    private VisitDAO visitDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Assertions.assertNotNull(visitService);
        Assertions.assertNotNull(visitDAO);
    }

    @Test
    public void testHasPatientSeenThisDoctor_PatientHasSeenDoctor_ReturnsTrue() {
        int doctorId = 1;
        int patientId = 1;
        List<VisitDTO> visits = new ArrayList<>();
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

    // TODO wróć do tego później z bazą danych dla testów
    @Test
    public void testFindAllVisitByDoctor() {
        int doctorId = 1;
        List<VisitDTO> expectedVisits = new ArrayList<>();
        VisitDTO visit1 = VisitDTO.builder().doctorId(doctorId).build();
        VisitDTO visit2 = VisitDTO.builder().doctorId(doctorId).build();
        VisitDTO visit3 = VisitDTO.builder().doctorId(doctorId).build();
        expectedVisits.add(visit1);
        expectedVisits.add(visit2);
        expectedVisits.add(visit3);

        when(visitDAO.findAllByDoctor(doctorId)).thenReturn(expectedVisits);

        List<VisitDTO> actualVisits = visitService.findAllVisitByDoctor(doctorId);

        assertEquals(expectedVisits, actualVisits);
        assertTrue(actualVisits.contains(visit2));
    }

    @Test
    public void testFindAllVisitByPatient() {
        int patientId = 1;
        List<VisitDTO> expectedVisits = new ArrayList<>();
        VisitDTO visit1 = VisitDTO.builder().patientId(patientId).build();
        VisitDTO visit2 = VisitDTO.builder().patientId(patientId).build();
        VisitDTO visit3 = VisitDTO.builder().patientId(patientId).build();
        expectedVisits.add(visit1);
        expectedVisits.add(visit2);
        expectedVisits.add(visit3);


        when(visitDAO.findAllByPatient(patientId)).thenReturn(expectedVisits);

        List<VisitDTO> actualVisits = visitService.findAllVisitByPatient(patientId);

        assertEquals(expectedVisits, actualVisits);
        assertEquals((int) actualVisits.get(1).getPatientId(), patientId);
    }

    @Test
    public void testFindAllVisitForBoth() {
        int doctorId = 1;
        int patientId = 1;
        List<VisitDTO> expectedVisits = new ArrayList<>();
        VisitDTO visit1 = VisitDTO.builder().doctorId(doctorId).patientId(patientId).build();
        VisitDTO visit2 = VisitDTO.builder().doctorId(doctorId).patientId(patientId).build();
        VisitDTO visit3 = VisitDTO.builder().doctorId(doctorId).patientId(patientId).build();
        expectedVisits.add(visit1);
        expectedVisits.add(visit2);
        expectedVisits.add(visit3);

        when(visitDAO.findAllForBoth(doctorId, patientId)).thenReturn(expectedVisits);

        List<VisitDTO> actualVisits = visitService.findAllVisitForBoth(doctorId, patientId);

        assertEquals(expectedVisits, actualVisits);
        assertTrue(actualVisits.stream().allMatch(visit -> visit.getDoctorId().equals(doctorId)));
    }
}