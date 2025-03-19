package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class FileUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UUID create(CreateUserRequest request) {
        boolean emailCheck = userRepository.findAll().stream()
            .anyMatch(u -> u.getEmail().equals(request.getEmail()));
        if (emailCheck) {
            throw new IllegalArgumentException("[ERROR]중복된 이메일 입니다.");
        }

        User user = new User(request.getName(), request.getEmail(), request.getPassword());
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return user.getId();
    }

    public UUID create(CreateUserRequest request, UUID profileImageId) {
        UUID uuid = create(request);
        User user = userRepository.findByUserId(uuid);

        user.updateProfileImageId(profileImageId);
        return user.getId();
    }

    @Override
    public UserResponseDto findByUserId(UUID userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UUID update(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findByUserId(userId);
        user.updateName(request.getName());
        user.updateEmail(request.getEmail());

        userRepository.save(user);

        return user.getId();
    }

    public UUID update(UUID userId, UpdateUserRequest request,
        CreateBinaryContentRequest binaryContentDto) {
        UUID uuid = update(userId, request);

        User user = userRepository.findByUserId(uuid);

        binaryContentRepository.delete(user.getProfileImageId());

        BinaryContent newBinaryContent = new BinaryContent(binaryContentDto.getBinaryImage());
        binaryContentRepository.save(newBinaryContent);
        user.updateProfileImageId(newBinaryContent.getId());

        return uuid;
    }

    @Override
    public void remove(UUID userId) {
        User user = userRepository.findByUserId(userId);

        userRepository.delete(userId);
        userStatusRepository.deleteByUserId(userId);
        binaryContentRepository.delete(user.getProfileImageId());
    }
}
