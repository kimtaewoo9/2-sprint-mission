package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.login.LoginForm;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginForm form) {
        UserResponseDto response = authService.login(form);

        return ResponseEntity.ok(response);
    }
}
