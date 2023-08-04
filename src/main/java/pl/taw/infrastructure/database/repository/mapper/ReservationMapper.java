package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.taw.api.dto.ReservationDTO;
import pl.taw.infrastructure.database.entity.ReservationEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    ReservationDTO mapFromEntity(ReservationEntity reservationEntity);

    ReservationEntity mapToEntity(ReservationDTO reservationDTO);

}


