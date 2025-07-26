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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.company.exceptions.constant.ErrorCode.USER_NOT_FOUND;
import static com.company.exceptions.constant.ErrorMessage.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        final List<User> userList = userRepository.findAll();
        logger.info("Fetched {} users", userList.size());

        return userList.stream().
                map(userMapper::toUserResponse).
                toList();
    }

    @Override
    @Cacheable(value = "USER_CACHE", key = "#id")
    public UserResponse getUserById(long id) {
        final User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFound(String.format(USER_NOT_FOUND_MESSAGE, id),
                        USER_NOT_FOUND));
        logger.info("User found with ID {}", id);

        return userMapper.toUserResponse(user);
    }

    @Override
    @CachePut(value = "USER_CACHE", key = "#result.id()")
    public UserResponse createUser(UserRequest request) {
        logger.info("creating user");
        final User user = userMapper.toUser(request);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @CachePut(value = "USER_CACHE", key = "#id")
    public UserResponse updateUser(Long id, UserRequest request) {
        logger.info("Updating user with ID {}", id);
        final User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFound(String.format(USER_NOT_FOUND_MESSAGE, id),
                        USER_NOT_FOUND)
                );
        userMapper.updateUserFromRequest(request, user);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @CachePut(value = "USER_CACHE", key = "#id")
    public UserResponse patchUser(Long id, Map<String, Object> updates) {
        logger.info("Patching user called with ID {} using updates: {}", id, updates);
        final User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFound(String.format(USER_NOT_FOUND_MESSAGE, id),
                        USER_NOT_FOUND)
                );
        final Class<?> clazz = user.getClass();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            final String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                logger.warn("Skipped patching field '{}' because value is null", fieldName);
                continue;
            }

            try {
                final Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                final Class<?> type = field.getType();

                if (field.isAnnotationPresent(NotBlank.class) && value instanceof String strVal) {
                    if (strVal.isBlank()) {
                        logger.warn("Patching field '{}' is blank  ", fieldName);
                        throw new IllegalArgumentException(fieldName + " must be not blank");
                    }
                }

                if (field.isAnnotationPresent(Size.class) && value instanceof String strVal) {
                    final Size size = field.getAnnotation(Size.class);
                    if (strVal.length() > size.max()) {
                        logger.warn("Patching field '{}' must be less than {}", fieldName, size.max() + 1);
                        throw new IllegalArgumentException(fieldName + " must be less than " +
                                (size.max() + 1));
                    }
                }

                if (type.equals(LocalDate.class) && value instanceof String strVal) {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    final LocalDate date = LocalDate.parse(strVal, formatter);

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
    @CacheEvict(value = "USER_CACHE", key = "#id")
    public void deleteUser(Long id) {
        logger.warn("Delete user with ID '{}'", id);
        final User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFound(String.format(USER_NOT_FOUND_MESSAGE, id),
                        USER_NOT_FOUND)
                );

        userRepository.delete(user);
    }


}
