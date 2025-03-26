package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.login.LoginForm;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    UserRepository userRepository;

    @Override
    public UserResponseDto login(LoginForm form) {

        String username = form.getName();
        String password = form.getPassword();

        User user = userRepository.findByUserName(username);
        boolean result = user.getPassword().equals(password);
        if (!result) {
            throw new IllegalArgumentException("[ERROR] 다시 입력 해주세요.");
        }

        user.updateLastLoginAt(Instant.now());

        UserResponseDto userResponseDto = UserResponseDto.from(user, true);

        return userResponseDto;
    }
}
