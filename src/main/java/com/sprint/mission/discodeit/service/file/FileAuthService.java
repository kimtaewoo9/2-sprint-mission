package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.login.LoginForm;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User login(LoginForm form) {

        String username = form.getUsername();
        String password = form.getPassword();

        User user = userRepository.findByUserName(username);
        boolean result = user.getPassword().equals(password);
        if (!result) {
            throw new IllegalArgumentException("[ERROR] 다시 입력 해주세요.");
        }

        user.updateLastLoginAt(Instant.now());

        return user;
    }
}
