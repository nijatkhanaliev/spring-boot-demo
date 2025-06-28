package com.company.service;

import com.company.models.dto.request.UserRequest;
import com.company.models.dto.response.UserResponse;
import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getUserById(long id);

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(Long id, UserRequest request);

    UserResponse patchUser(Long id, Map<String, Object> patchRequest);

    void deleteUser(Long id);
}
