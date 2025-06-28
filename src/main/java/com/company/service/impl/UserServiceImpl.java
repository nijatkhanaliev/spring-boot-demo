package com.company.service.impl;

import com.company.exceptions.UserNotFound;
import com.company.models.dto.request.UserRequest;
import com.company.models.dto.response.UserResponse;
import com.company.models.entity.User;
import com.company.models.mapper.UserMapper;
import com.company.repository.UserRepository;
import com.company.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> userList = userRepository.findAll();

        return userList.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("user not exists with id: " + id));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        System.out.println(request.getFirstName());
        User user = userMapper.toUser(request);
        System.out.println(user.getFirstName());
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User not exists with id: " + id));

        userMapper.updateUserFromRequest(request, user);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse patchUser(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User not exists with id: " + id));

        Class<?> clazz = user.getClass();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if(value==null){
                continue;
            }

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                Class<?> type = field.getType();

                if (field.isAnnotationPresent(NotBlank.class) && value instanceof String strVal) {
                    if (strVal.isBlank()) {
                        throw new IllegalArgumentException(fieldName + " must be not blank");
                    }
                }

                if (field.isAnnotationPresent(Size.class) && value instanceof String strVal) {
                    Size size = field.getAnnotation(Size.class);
                    if(strVal.length()>size.max()){
                        throw new IllegalArgumentException(fieldName + " must be less than " + (size.max()+1));
                    }
                }

                if(type.equals(LocalDate.class) && value instanceof String strVal ){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate date = LocalDate.parse(strVal,formatter);

                    if(date.isAfter(LocalDate.now())){
                        throw new IllegalArgumentException(fieldName + " must be before now");
                    }
                    value = date;
                }


                field.set(user,value);

            } catch (NoSuchFieldException e) {
                throw new RuntimeException("No such filed: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Illegal access exception to field: " + fieldName);
            }

        }
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
       User user =   userRepository.findById(id)
                .orElseThrow(()-> new UserNotFound("User not exists with id: " + id));

       userRepository.delete(user);
    }


}
