package project.booteco.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import project.booteco.domain.User;
import project.booteco.pruducer.UserGetResponse;
import project.booteco.pruducer.UserPostRequest;
import project.booteco.pruducer.UserPutResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stateConversation", ignore = true)
    @Mapping(target = "objectiveTextFree", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "urlGraphic", ignore = true)
    @Mapping(target = "emailGoogle", ignore = true)
    User toEntity (UserPostRequest request);

    UserGetResponse toResponse(User user);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "phoneWhatsapp", ignore = true)
    void updateEntity(UserPutResponse request, @MappingTarget User user);


}
