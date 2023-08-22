package pl.taw.api.controller.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.OpinionsDTO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpinionRestControllerMockitoTest {

    @Mock
    private OpinionDAO opinionDAO;

    @InjectMocks
    private OpinionRestController opinionRestController;


    @Test
     void getOpinionsShouldWorksCorrectly() {
        // given
        OpinionsDTO opinions = OpinionsDTO.of(DtoFixtures.opinions);

        when(opinionDAO.findAll()).thenReturn(opinions.getOpinions());

        // when
        OpinionsDTO result = opinionRestController.getOpinions();

        // then
        assertThat(result, is(notNullValue()));
        assertThat(result.getOpinions(), hasSize(opinions.getOpinions().size()));
        assertThat(result.getOpinions(), containsInAnyOrder(opinions.getOpinions().toArray()));

        verify(opinionDAO, times(1)).findAll();
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    void testGetOpinionDetailsShouldWorksCorrectly() {
        // given
        Integer opinionId = 1;
        OpinionDTO opinion = DtoFixtures.someOpinion1();

        when(opinionDAO.findById(opinionId)).thenReturn(opinion);

        // when
        OpinionDTO result = opinionRestController.getOpinionDetails(opinionId);

        // then
        assertThat(result, is(notNullValue()));
        assertEquals(opinion, result);

        verify(opinionDAO, times(1)).findById(opinionId);
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    void testGetOpinionDetailsShouldReturnNotFound() {
        // given
        Integer opinionId = -88;

        when(opinionDAO.findById(anyInt())).thenReturn(null);

        // when
        OpinionDTO result = opinionRestController.getOpinionDetails(opinionId);

        // then
        assertNull(result);

        verify(opinionDAO, times(1)).findById(opinionId);
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    void testGetAllOpinionCommentsShouldWorksCorrectly() {
        // given
        List<OpinionDTO> opinions = DtoFixtures.opinions;
        List<String> comments = opinions.stream().map(OpinionDTO::getComment).toList();

        when(opinionDAO.findAll()).thenReturn(opinions);

        // when
        ResponseEntity<List<String>> response = opinionRestController.getAllOpinionComments();

        // then
        assertThat(response, is(notNullValue()));
        assertEquals(opinions.size(), Objects.requireNonNull(response.getBody()).size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(opinions.get(0).getComment(), response.getBody().get(0));
        assertEquals(opinions.get(1).getComment(), response.getBody().get(1));
        assertTrue(response.getBody().containsAll(comments));

        verify(opinionDAO, times(1)).findAll();
        verify(opinionDAO, only()).findAll();
    }

    @Test
    void createOpinionShouldWorksCorrectly() {
        // given
        OpinionEntity opinionEntity = EntityFixtures.someOpinion1();

        when(opinionDAO.saveAndReturn(opinionEntity)).thenReturn(opinionEntity);

        // when
        ResponseEntity<OpinionEntity> response = opinionRestController.createOpinion(opinionEntity);

        // then
        assertThat(response, is(notNullValue()));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(opinionEntity, response.getBody());

        verify(opinionDAO, times(1)).saveAndReturn(opinionEntity);
        verify(opinionDAO, only()).saveAndReturn(opinionEntity);
    }

    @Test
    void createOpinionShouldReturnCreatedResponse() {
        // given
        OpinionEntity opinionEntity = EntityFixtures.someOpinion1();

        when(opinionDAO.saveAndReturn(opinionEntity)).thenReturn(opinionEntity);

        // when
        ResponseEntity<OpinionEntity> response = opinionRestController.createOpinion(opinionEntity);

        // then
        assertThat(response, is(notNullValue()));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(opinionDAO, times(1)).saveAndReturn(opinionEntity);
        verify(opinionDAO, only()).saveAndReturn(opinionEntity);
    }

    @Test
    void updateOpinionShouldReturnUpdatedOpinion() {
        // given
        Integer opinionId = 2;
        OpinionEntity existingOpinion = EntityFixtures.someOpinion2();
        OpinionEntity updatedOpinion = EntityFixtures.someOpinion2().withComment("Nowy komentarz dla opinii");

        when(opinionDAO.findEntityById(opinionId)).thenReturn(existingOpinion);
        when(opinionDAO.saveAndReturn(updatedOpinion)).thenReturn(updatedOpinion);

        // when
        ResponseEntity<OpinionEntity> response = opinionRestController.updateOpinion(opinionId, updatedOpinion);

        // then
        assertThat(response, is(notNullValue()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOpinion, response.getBody());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, times(1)).saveAndReturn(existingOpinion);
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    void updateOpinionShouldReturnNotFoundForNonExistingOpinion() {
        // given
        Integer opinionId = 1;
        OpinionEntity updatedOpinion = EntityFixtures.someOpinion1().withComment("Zmieniony komentarz");

        when(opinionDAO.findEntityById(opinionId)).thenReturn(null);

        // when
        ResponseEntity<OpinionEntity> response = opinionRestController.updateOpinion(opinionId, updatedOpinion);

        // then
        assertThat(response, is(notNullValue()));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, only()).findEntityById(opinionId);
    }

    @Test
    void deleteOpinionByIdShouldReturnNoContentForExistingOpinion() {
        // given
        Integer opinionId = 1;
        OpinionEntity existingOpinion = EntityFixtures.someOpinion1();

        when(opinionDAO.findEntityById(opinionId)).thenReturn(existingOpinion);

        // when
        ResponseEntity<Void> response = opinionRestController.deleteOpinionById(opinionId);

        // then
        assertThat(response, is(notNullValue()));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, only()).findEntityById(opinionId);
    }

    @Test
    void deleteOpinionByIdShouldReturnNotFoundForNonExistingOpinion() {
        // given
        Integer opinionId = 1;

        when(opinionDAO.findEntityById(opinionId)).thenReturn(null);

        // when
        ResponseEntity<Void> response = opinionRestController.deleteOpinionById(opinionId);

        // then
        assertThat(response, is(notNullValue()));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, never()).delete(any());
        verify(opinionDAO, only()).findEntityById(opinionId);
    }

    @Test
    void updateOpinionCommentShouldReturnOkForExistingOpinion() {
        // given
        Integer opinionId = 2;
        String updatedComment = "Aktualizowany komentarz";
        OpinionEntity existingOpinion = EntityFixtures.someOpinion2();

        when(opinionDAO.findEntityById(opinionId)).thenReturn(existingOpinion);

        // when
        ResponseEntity<?> response = opinionRestController.updateOpinionComment(opinionId, updatedComment);

        // then
        assertThat(response, is(notNullValue()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        assertEquals(updatedComment, existingOpinion.getComment());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, times(1)).save(existingOpinion);
        verifyNoMoreInteractions(opinionDAO);
    }

}