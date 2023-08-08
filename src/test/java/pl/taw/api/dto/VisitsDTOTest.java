package pl.taw.api.dto;

import org.junit.jupiter.api.Test;
import pl.taw.util.DtoFixtures;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitsDTOTest {

    @Test
    public void testVisitsDTO() {
        // given
        LocalDateTime dateTime1 = LocalDateTime.of(2023, 8, 10, 14, 30);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 8, 10, 16, 0);

        VisitDTO visit1 = DtoFixtures.someVisit1().withDateTime(dateTime1);
        VisitDTO visit2 = DtoFixtures.someVisit2().withDateTime(dateTime2);

        List<VisitDTO> visitsList = new ArrayList<>();
        visitsList.add(visit1);
        visitsList.add(visit2);

        // when
        VisitsDTO visitsDTO = VisitsDTO.of(visitsList);

        // then
        assertNotNull(visitsDTO);
        assertEquals(2, visitsDTO.getVisits().size());
        assertEquals(visit1, visitsDTO.getVisits().get(0));
        assertEquals(visit2, visitsDTO.getVisits().get(1));
        assertTrue(visitsDTO.getVisits().get(0).getDateTime().isBefore(visitsDTO.getVisits().get(1).getDateTime()));
    }

}