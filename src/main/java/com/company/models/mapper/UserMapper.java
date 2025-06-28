package com.company.models.mapper;

import com.company.models.dto.request.UserRequest;
import com.company.models.dto.response.UserResponse;
import com.company.models.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRequest userRequest);

    UserResponse toUserResponse(User user);

    void updateUserFromRequest(UserRequest request, @MappingTarget User user);

}
