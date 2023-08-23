package pl.taw.api.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.taw.api.dto.OpinionDTO;
import pl.taw.api.dto.OpinionsDTO;
import pl.taw.business.dao.OpinionDAO;
import pl.taw.infrastructure.database.entity.OpinionEntity;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.taw.api.controller.rest.OpinionRestController.*;


@WebMvcTest(controllers = OpinionRestController.class)
@AutoConfigureMockMvc(addFilters = false) // wyłączenie filtrów security dla testów Mock Mvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
class OpinionRestControllerWebMvcTest {

    @MockBean
    private OpinionDAO opinionDAO;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Test
    void getOpinionsShouldWorksCorrectly() throws Exception {
        // given
        OpinionsDTO opinions = OpinionsDTO.of(DtoFixtures.opinions);

        when(opinionDAO.findAll()).thenReturn(opinions.getOpinions());

        // when, then
        mockMvc.perform(get(API_OPINIONS))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opinions", hasSize(opinions.getOpinions().size())))
                .andExpect(jsonPath("$.opinions[*].opinionId", notNullValue()))
                .andExpect(jsonPath("$.opinions[*].comment", notNullValue()))
                .andExpect(jsonPath("$.opinions[*].createdAt", notNullValue()))
                .andExpect(jsonPath("$.opinions[*].visitId", notNullValue()))
                .andExpect(jsonPath("$.opinions[*].doctorId", notNullValue()))
                .andExpect(jsonPath("$.opinions[*].patientId", notNullValue()));

        verify(opinionDAO, times(1)).findAll();
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    void testGetOpinionDetailsShouldWorksCorrectly() throws Exception {
        // given
        Integer opinionId = 1;
        OpinionDTO opinion = DtoFixtures.someOpinion1();

        when(opinionDAO.findById(opinionId)).thenReturn(opinion);

        // when, then
        mockMvc.perform(get(API_OPINIONS.concat(OPINION_ID), opinionId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opinionId", is(opinionId)))
                .andExpect(jsonPath("$.doctorId", is(opinion.getDoctorId())))
                .andExpect(jsonPath("$.patientId", is(opinion.getPatientId())))
                .andExpect(jsonPath("$.visitId", is(opinion.getVisitId())))
                .andExpect(jsonPath("$.comment", is(opinion.getComment())))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(opinionDAO, times(1)).findById(opinionId);
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    void testGetAllOpinionCommentsShouldWorksCorrectly() throws Exception {
        // given
        List<OpinionDTO> opinions = DtoFixtures.opinions;
        List<String> comments = opinions.stream().map(OpinionDTO::getComment).toList();

        when(opinionDAO.findAll()).thenReturn(opinions);

        // when, then
        mockMvc.perform(get(API_OPINIONS.concat(COMMENTS)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(comments.size())))
                .andExpect(jsonPath("$[0]", is(comments.get(0))))
                .andExpect(jsonPath("$[*]", notNullValue()));

        verify(opinionDAO, times(1)).findAll();
        verify(opinionDAO, only()).findAll();
    }

    @Test
    void createOpinionShouldWorksCorrectly() throws Exception {
        // given
        OpinionEntity opinionEntity = EntityFixtures.someOpinion1();

        when(opinionDAO.saveAndReturn(opinionEntity)).thenReturn(opinionEntity);

        // when, then
        mockMvc.perform(post(API_OPINIONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opinionEntity)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.opinionId", notNullValue()));

        verify(opinionDAO, times(1)).saveAndReturn(opinionEntity);
        verify(opinionDAO, only()).saveAndReturn(opinionEntity);
    }

    @Test
    void updateOpinionShouldReturnUpdatedOpinion() throws Exception {
        // given
        Integer opinionId = 2;
        OpinionEntity existingOpinion = EntityFixtures.someOpinion2();
        OpinionEntity updatedOpinion = EntityFixtures.someOpinion2().withComment("Nowy komentarz dla opinii");

        when(opinionDAO.findEntityById(opinionId)).thenReturn(existingOpinion);
        when(opinionDAO.saveAndReturn(updatedOpinion)).thenReturn(updatedOpinion);

        // when, then
        mockMvc.perform(put(API_OPINIONS.concat(UPDATE_BY_ID), opinionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOpinion)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opinionId", is(updatedOpinion.getOpinionId())))
                .andExpect(jsonPath("$.doctorId", is(updatedOpinion.getDoctorId())));

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, times(1)).saveAndReturn(existingOpinion);
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    void updateOpinionShouldReturnNotFoundForNonExistingOpinion() throws Exception {
        // given
        Integer opinionId = 1;
        OpinionEntity updatedOpinion = EntityFixtures.someOpinion1().withComment("Zmieniony komentarz");

        when(opinionDAO.findEntityById(opinionId)).thenReturn(null);

        // when, then
        mockMvc.perform(put(API_OPINIONS.concat(UPDATE_BY_ID), opinionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOpinion)))
                .andExpect(status().isNotFound());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, only()).findEntityById(opinionId);
    }

    @Test
    void deleteOpinionByIdShouldReturnNoContentForExistingOpinion() throws Exception {
        // given
        Integer opinionId = 1;
        OpinionEntity existingOpinion = EntityFixtures.someOpinion1();

        when(opinionDAO.findEntityById(opinionId)).thenReturn(existingOpinion);

        // when, then
        mockMvc.perform(delete(API_OPINIONS.concat(OPINION_ID), opinionId))
                        .andExpect(status().isNoContent());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, times(1)).delete(existingOpinion);
        verifyNoMoreInteractions(opinionDAO);
    }

    @Test
    void deleteOpinionByIdShouldReturnNotFoundForNonExistingOpinion() throws Exception {
        // given
        Integer opinionId = 1;

        when(opinionDAO.findEntityById(opinionId)).thenReturn(null);

        // when, then
        mockMvc.perform(delete(API_OPINIONS.concat(OPINION_ID), opinionId))
                .andExpect(status().isNotFound());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, never()).delete(any());
        verify(opinionDAO, only()).findEntityById(opinionId);
    }

    @Test
    void updateOpinionCommentShouldReturnOkForExistingOpinion() throws Exception {
        // given
        Integer opinionId = 2;
        String updatedComment = "Aktualizowany komentarz";
        OpinionEntity existingOpinion = EntityFixtures.someOpinion2();

        when(opinionDAO.findEntityById(opinionId)).thenReturn(existingOpinion);

        // when, then
        mockMvc.perform(patch(API_OPINIONS.concat(OPINION_UPDATE_NOTE), opinionId)
                .param("updatedComment", updatedComment))
                .andExpect(status().isOk());

        verify(opinionDAO, times(1)).findEntityById(opinionId);
        verify(opinionDAO, times(1)).save(existingOpinion);
        verifyNoMoreInteractions(opinionDAO);
    }

}