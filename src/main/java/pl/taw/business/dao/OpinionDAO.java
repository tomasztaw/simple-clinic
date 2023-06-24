package pl.taw.business.dao;

import pl.taw.api.dto.OpinionDTO;
import pl.taw.infrastructure.database.entity.OpinionEntity;

import java.util.List;

public interface OpinionDAO {

    List<OpinionDTO> findAll();

    OpinionDTO findById(Integer opinionId);

    OpinionEntity findEntityById(Integer opinionId);

    OpinionEntity saveAndReturn(OpinionEntity opinionEntity);

    void save(OpinionEntity opinionEntity);

    void delete(OpinionEntity opinionEntity);

    List<OpinionDTO> findAllByDoctor(Integer doctorId);

    List<OpinionDTO> findAllByPatient(Integer patientId);

}
