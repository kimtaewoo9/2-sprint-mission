package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.login.LoginForm;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class FileAuthService implements AuthService {

    UserRepository userRepository;
    
    @Override
    public User login(LoginForm form) {

        User user = userRepository.findByUserName(form.getName());
        if (user == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 사용자 정보입니다.");
        }

        boolean result = user.getPassword().equals(form.getPassword());
        if (!result) {
            throw new IllegalArgumentException("[ERROR] 잘못된 비밀번호 입니다.");
        }
        // 로그인 처리 .

        return user;
    }
}
