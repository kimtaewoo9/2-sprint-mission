package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.exception.custom.DuplicateResourceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UUID create(CreateUserRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("[ERROR] user name already exist");
        }
        if (userRepository.existByEmail(email)) {
            throw new DuplicateResourceException("[ERROR] email already exist");
        }
        String password = request.getPassword();

        User user = new User(username, email, password);
        userRepository.save(user);
        User createdUser = userRepository.findByUserId(user.getId());

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(createdUser.getId(), now);
        userStatusRepository.save(userStatus);

        return createdUser.getId();
    }

    @Override
    public UUID create(CreateUserRequest request, CreateBinaryContentRequest binaryContentRequest) {
        UUID uuid = create(request);
        User user = userRepository.findByUserId(uuid);

        String fileName = binaryContentRequest.getFileName();
        String contentType = binaryContentRequest.getContentType();
        byte[] bytes = binaryContentRequest.getBytes();
        int size = bytes.length;

        BinaryContent binaryContent = new BinaryContent(fileName, size, contentType, bytes);
        binaryContentRepository.save(binaryContent);

        user.updateProfileImageId(binaryContent.getId());

        return user.getId();
    }

    @Override
    public UserResponseDto findByUserId(UUID userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new NoSuchElementException("[ERROR] user not found");
        }

        boolean isOnline = userStatusRepository.findByUserId(userId).isOnline();

        return UserResponseDto.from(user, isOnline);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
            .map(u -> UserResponseDto.from(u,
                userStatusRepository.findByUserId(u.getId()).isOnline())).toList();
    }

    @Override
    public UUID update(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new NoSuchElementException("[ERROR] user not found");
        }

        String username = request.getNewUsername();
        String email = request.getNewEmail();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("[ERROR] user name already exist");
        }
        if (userRepository.existByEmail(email)) {
            throw new DuplicateResourceException("[ERROR] email already exist");
        }

        String password = request.getNewPassword();

        user.updateName(username);
        user.updateEmail(email);
        user.updatePassword(password);

        userRepository.save(user);

        return user.getId();
    }

    @Override
    public UUID update(UUID userId, UpdateUserRequest request,
        CreateBinaryContentRequest binaryContentRequest) {

        UUID findUser = update(userId, request);

        User user = userRepository.findByUserId(findUser); // 계속 findByUserId로 찾아야함.
        UUID profileImageId = user.getProfileImageId();
        if (profileImageId != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }

        String fileName = binaryContentRequest.getFileName();
        String contentType = binaryContentRequest.getContentType();
        byte[] bytes = binaryContentRequest.getBytes();
        long length = bytes.length;

        BinaryContent binaryContent = new BinaryContent(
            fileName,
            length,
            contentType,
            bytes
        );

        user.updateProfileImageId(binaryContent.getId());
        userRepository.save(user);

        return user.getId();
    }

    @Override
    public void remove(UUID userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new NoSuchElementException("[ERROR] user not found");
        }
        userRepository.delete(userId);
        userStatusRepository.deleteByUserId(userId);
        
        if (user.getProfileImageId() != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }
    }
}
