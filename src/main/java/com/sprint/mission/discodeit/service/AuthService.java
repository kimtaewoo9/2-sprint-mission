package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.login.LoginForm;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    UserResponseDto login(LoginForm form);
}
