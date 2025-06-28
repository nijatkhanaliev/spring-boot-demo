package com.company.service.impl;

import com.company.exceptions.UserNotFound;
import com.company.models.dto.request.UserRequest;
import com.company.models.dto.response.UserResponse;
import com.company.models.entity.User;
import com.company.models.mapper.UserMapper;
import com.company.repository.UserRepository;
import com.company.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        logger.debug("Fetching all users");
        List<User> userList = userRepository.findAll();
        logger.info("Fetched {} users", userList.size());

        return userList.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(long id) {
        logger.debug("Fetching user by ID {}", id);
        User user = userRepository.findById(id).orElseThrow(
                () -> {
                    logger.error("User not found with ID {}", id);
                    return new UserNotFound("user not exists with id: " + id);
                }

        );
        logger.info("User found with ID {}", id);

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        logger.debug("Creating user with request: {}", request);
        User user = userMapper.toUser(request);
        userRepository.save(user);
        logger.info("User created successfully: {}", user.getId());

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        logger.debug("Updating user with ID {} using request: {}", id, request);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                            logger.error("User not found with ID {}", id);
                            return new UserNotFound("User not exists with id: " + id);
                        }
                );
        userMapper.updateUserFromRequest(request, user);

        userRepository.save(user);
        logger.info("User updated with ID {}", id);

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse patchUser(Long id, Map<String, Object> updates) {
        logger.debug("Patching user called with ID {} using updates: {}", id, updates);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                            logger.error("User not found with ID {}", id);
                            return new UserNotFound("User not exists with id: " + id);
                        }
                );
        Class<?> clazz = user.getClass();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                logger.warn("Skipped patching field '{}' because value is null", fieldName);
                continue;
            }

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                Class<?> type = field.getType();

                if (field.isAnnotationPresent(NotBlank.class) && value instanceof String strVal) {
                    if (strVal.isBlank()) {
                        logger.error("Patching field '{}' is blank  ", fieldName);
                        throw new IllegalArgumentException(fieldName + " must be not blank");
                    }
                }

                if (field.isAnnotationPresent(Size.class) && value instanceof String strVal) {
                    Size size = field.getAnnotation(Size.class);
                    if (strVal.length() > size.max()) {
                        logger.error("Patching field '{}' must be less than {}", fieldName, size.max() + 1);
                        throw new IllegalArgumentException(fieldName + " must be less than " + (size.max() + 1));
                    }
                }

                if (type.equals(LocalDate.class) && value instanceof String strVal) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate date = LocalDate.parse(strVal, formatter);

                    if (date.isAfter(LocalDate.now())) {
                        logger.error("Patching field '{}' must be before now", fieldName);
                        throw new IllegalArgumentException(fieldName + " must be before now");
                    }
                    value = date;
                }


                field.set(user, value);
                logger.info("Field '{}' patched with value: {} ", fieldName, value);
            } catch (NoSuchFieldException e) {
                logger.error("Failed to patch field '{}': {} ", fieldName, e.getMessage());
                throw new RuntimeException("No such filed: " + fieldName);
            } catch (IllegalAccessException e) {
                logger.error("Failed to patch field '{}': {} ", fieldName, e.getMessage());
                throw new RuntimeException("Illegal access exception to field: " + fieldName);
            }

        }
        userRepository.save(user);
        logger.info("User patched successfully with ID '{}'", id);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        logger.debug("Delete user with ID '{}'", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                            logger.error("User not found with ID '{}'", id);
                            return new UserNotFound("User not exists with id: " + id);
                        }

                );
        logger.info("User deleted  successfully with ID '{}'", id);
        userRepository.delete(user);
    }


}
