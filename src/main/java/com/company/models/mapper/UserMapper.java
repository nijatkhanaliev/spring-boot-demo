package com.company.models.mapper;

import com.company.models.dto.request.UserRequest;
import com.company.models.dto.response.UserResponse;
import com.company.models.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //@Mapping(target = "status",constant = "ACTIVE")
    ///@Mapping(target = "id",ignore = true)
    User toUser(UserRequest userRequest);

    @Mapping(target = "fullName",source = "user",qualifiedByName = "getFullName")
    UserResponse toUserResponse(User user);

    void updateUserFromRequest(UserRequest request, @MappingTarget User user);

    @Named("getFullName")
    default String getFullName(User user){
        return user.getFirstName() + " " + user.getLastName();
    }

}
