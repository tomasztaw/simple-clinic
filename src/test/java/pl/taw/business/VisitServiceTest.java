package pl.taw.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.repository.VisitRepository;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;
import pl.taw.infrastructure.database.repository.mapper.VisitMapperImpl;
import pl.taw.util.EntityFixtures;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitService visitService;

    private VisitMapper visitMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        visitMapper = new VisitMapperImpl();
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

}