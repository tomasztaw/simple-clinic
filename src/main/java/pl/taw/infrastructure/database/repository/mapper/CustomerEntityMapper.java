package pl.taw.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import pl.taw.infrastructure.database.entity.CarServiceRequestEntity;
import pl.taw.infrastructure.database.entity.CustomerEntity;
import pl.taw.infrastructure.database.entity.InvoiceEntity;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerEntityMapper {
//
//    @Mapping(target = "address.customer", ignore = true)
//    @Mapping(source = "carServiceRequests", target = "carServiceRequests", qualifiedByName = "mapCarServiceRequests")
//    @Mapping(source = "invoices", target = "invoices", qualifiedByName = "mapInvoices")
//    Customer mapFromEntity(CustomerEntity entity);
//
//    @Named("mapInvoices")
//    @SuppressWarnings("unused")
//    default Set<Invoice> mapInvoices(Set<InvoiceEntity> invoiceEntities) {
//        return invoiceEntities.stream().map(this::mapFromEntity).collect(Collectors.toSet());
//    }
//
//    @Named("mapCarServiceRequests")
//    @SuppressWarnings("unused")
//    default Set<CarServiceRequest> mapCarServiceRequests(Set<CarServiceRequestEntity> entities) {
//        return entities.stream().map(this::mapFromEntity).collect(Collectors.toSet());
//    }
//
//    @Mapping(target = "car", ignore = true)
//    @Mapping(target = "customer", ignore = true)
//    @Mapping(target = "serviceMechanics", ignore = true)
//    @Mapping(target = "serviceParts", ignore = true)
//    CarServiceRequest mapFromEntity(CarServiceRequestEntity entity);
//
//    @Mapping(target = "car", ignore = true)
//    @Mapping(target = "customer", ignore = true)
//    @Mapping(target = "salesman", ignore = true)
//    Invoice mapFromEntity(InvoiceEntity entity);
//
//    @Mapping(target = "carServiceRequests", ignore = true)
//    CustomerEntity mapToEntity(Customer customer);
}
