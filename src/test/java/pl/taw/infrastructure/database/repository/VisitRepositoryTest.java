package pl.taw.infrastructure.database.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.taw.api.dto.VisitDTO;
import pl.taw.infrastructure.database.entity.VisitEntity;
import pl.taw.infrastructure.database.repository.jpa.VisitJpaRepository;
import pl.taw.infrastructure.database.repository.mapper.VisitMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisitRepositoryTest {

    @Mock
    private VisitJpaRepository visitJpaRepository;

    @Mock
    private VisitMapper visitMapper;

    @InjectMocks
    private VisitRepository visitRepository;

    @Test
    public void testFindAll() {
        // when
        List<VisitEntity> visitEntities = new ArrayList<>();
        visitEntities.add(new VisitEntity());
        visitEntities.add(new VisitEntity());

        Mockito.when(visitJpaRepository.findAll()).thenReturn(visitEntities);

        List<VisitDTO> expectedVisitDTOs = new ArrayList<>();
        expectedVisitDTOs.add(new VisitDTO());
        expectedVisitDTOs.add(new VisitDTO());

        Mockito.when(visitMapper.mapFromEntity(Mockito.any(VisitEntity.class)))
                .thenReturn(new VisitDTO());

        // then
        List<VisitDTO> result = visitRepository.findAll();

        // given
        Assertions.assertEquals(expectedVisitDTOs.size(), result.size());
        // Add more specific assertions if needed
    }

    @Test
    public void testFindById() {
        // when
        Integer visitId = 1;
        VisitEntity visitEntity = new VisitEntity();
        visitEntity.setVisitId(visitId);

        Mockito.when(visitJpaRepository.findById(visitId)).thenReturn(Optional.of(visitEntity));

        VisitDTO expectedVisitDTO = new VisitDTO();
        expectedVisitDTO.setVisitId(visitId);

        Mockito.when(visitMapper.mapFromEntity(visitEntity)).thenReturn(expectedVisitDTO);

        // then
        VisitDTO result = visitRepository.findById(visitId);

        // given
        Assertions.assertEquals(visitId, result.getVisitId());
        // Add more specific assertions if needed
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
}
