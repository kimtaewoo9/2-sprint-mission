package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.binarycontent.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.custom.DuplicateResourceException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(BasicUserService.class);

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    private final UserMapper userMapper;

    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public UserDto create(CreateUserRequest request) {
        logger.info("Creating new user with username: {}", request.username());
        logger.debug("User creation details - email: {}", request.email());

        try {
            validateUserUniqueness(request.username(), request.email());

            User user = User.createUser(
                request.username(), request.email(), request.password());
            User savedUser = userRepository.save(user);

            Instant now = Instant.now();
            UserStatus userStatus = UserStatus.createUserStatus(savedUser, now);
            userStatusRepository.save(userStatus);

            logger.info("User created successfully with ID: {}", savedUser.getId());
            return userMapper.toDto(user);
        } catch (DuplicateResourceException e) {
            logger.warn("Failed to create user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user creation: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserDto create(CreateUserRequest request,
        CreateBinaryContentRequest binaryContentRequest) {

        logger.info("Creating new user with username: {} and profile image", request.username());
        logger.debug("User creation details - email: {}, image filename: {}",
            request.email(), binaryContentRequest.fileName());

        try {
            validateUserUniqueness(request.username(), request.email());

            User user = User.createUser(
                request.username(), request.email(), request.password());
            User savedUser = userRepository.save(user);

            Instant now = Instant.now();
            UserStatus userStatus = UserStatus.createUserStatus(savedUser, now);
            savedUser.setStatus(userStatus);
            userStatusRepository.save(userStatus);

            String fileName = binaryContentRequest.fileName();
            String contentType = binaryContentRequest.contentType();
            byte[] bytes = binaryContentRequest.bytes();
            long size = bytes.length;

            logger.debug("Creating binary content for user profile - size: {} bytes", size);
            BinaryContent binaryContent =
                BinaryContent.createBinaryContent(fileName, size, contentType);
            BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

            binaryContentStorage.put(binaryContent.getId(), bytes);

            BinaryContent oldProfile = user.updateProfile(savedBinaryContent);
            if (oldProfile != null) {
                logger.debug("Deleting old profile image with ID: {}", oldProfile.getId());
                binaryContentStorage.deleteById(oldProfile.getId());
            }

            logger.info("User created successfully with ID: {} and profile image",
                savedUser.getId());
            return userMapper.toDto(user);
        } catch (DuplicateResourceException e) {
            logger.warn("Failed to create user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user creation with profile: {}", e.getMessage(),
                e);
            throw e;
        }
    }

    private void validateUserUniqueness(String username, String email) {
        logger.debug("Validating user uniqueness - username: {}, email: {}", username, email);
        if (userRepository.existsByUsername(username)) {
            logger.warn("Username already exists: {}", username);
            throw new DuplicateResourceException("[ERROR] user name already exists");
        }
        if (userRepository.existsByEmail(email)) {
            logger.warn("Email already exists: {}", email);
            throw new DuplicateResourceException("[ERROR] email already exists");
        }
    }

    @Override
    @Transactional
    public UserDto findByUserId(UUID userId) {
        logger.info("Finding user by ID: {}", userId);
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                logger.warn("User not found with ID: {}", userId);
                throw new NoSuchElementException("[ERROR] user not found");
            }

            logger.debug("User found: {}", user.getUsername());
            return userMapper.toDto(user);
        } catch (NoSuchElementException e) {
            logger.warn("User lookup failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user lookup: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<UserDto> findAll() {
        logger.info("Retrieving all users");
        try {
            List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toDto).toList();
            logger.debug("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Failed to retrieve all users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserDto update(UUID userId, UpdateUserRequest request) {
        logger.info("Updating user with ID: {}", userId);
        logger.debug("Update details - new username: {}, new email: {}",
            request.newUsername(), request.newEmail());

        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                logger.warn("User not found with ID: {}", userId);
                throw new NoSuchElementException("[ERROR] user not found");
            }

            String oldUsername = user.getUsername();
            user.update(request.newUsername(), request.newEmail(), request.newPassword());

            logger.info("User updated successfully: {} -> {}", oldUsername, user.getUsername());
            return userMapper.toDto(user);
        } catch (NoSuchElementException e) {
            logger.warn("User update failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user update: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserDto update(UUID userId, UpdateUserRequest request,
        CreateBinaryContentRequest binaryContentRequest) {

        logger.info("Updating user with ID: {} including profile image", userId);
        logger.debug("Update details - new username: {}, new email: {}, image filename: {}",
            request.newUsername(), request.newEmail(), binaryContentRequest.fileName());

        try {
            User user = userRepository.findById(userId).orElseThrow();
            String oldUsername = user.getUsername();
            user.update(request.newUsername(), request.newEmail(), request.newPassword());

            String fileName = binaryContentRequest.fileName();
            String contentType = binaryContentRequest.contentType();
            byte[] bytes = binaryContentRequest.bytes();
            long size = bytes.length;

            logger.debug("Creating new profile image - size: {} bytes", size);
            BinaryContent newProfile =
                BinaryContent.createBinaryContent(fileName, size, contentType);
            binaryContentRepository.save(newProfile);
            BinaryContent oldProfile = user.updateProfile(newProfile);

            binaryContentStorage.put(newProfile.getId(), bytes);
            if (oldProfile != null) {
                logger.debug("Deleting old profile image with ID: {}", oldProfile.getId());
                binaryContentStorage.deleteById(oldProfile.getId());
            }

            logger.info("User updated successfully: {} -> {} with new profile image",
                oldUsername, user.getUsername());
            return userMapper.toDto(user);
        } catch (NoSuchElementException e) {
            logger.warn("User update with profile failed: User not found with ID: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user update with profile: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void remove(UUID userId) {
        logger.info("Removing user with ID: {}", userId);

        try {
            User user = userRepository.findById(userId).orElseThrow();
            logger.debug("Found user to remove: {}", user.getUsername());

            BinaryContent profile = user.getProfile();
            if (profile != null) {
                logger.debug("Deleting profile image with ID: {}", profile.getId());
                binaryContentStorage.deleteById(profile.getId());
            }

            userRepository.delete(user);
            logger.info("User removed successfully: {}", userId);
            // user status, binary content는 자동으로 삭제
        } catch (NoSuchElementException e) {
            logger.warn("User removal failed: User not found with ID: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user removal: {}", e.getMessage(), e);
            throw e;
        }
    }
}
