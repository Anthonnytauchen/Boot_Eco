package project.booteco.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import project.booteco.domain.Transaction;
import project.booteco.pruducer.TransactionGetResponse;
import project.booteco.pruducer.TransactionPostRequest;
import project.booteco.pruducer.TransactionPutResponse;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "shortCode", ignore = true)
        //Estou transaformando um Resquest em uma entidade
    Transaction toEntity(TransactionPostRequest request);

    TransactionGetResponse toResponse(Transaction transaction);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "shortCode", ignore = true)
    void updateEntity(TransactionPutResponse request, @MappingTarget Transaction transaction);
}
