package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/findAll")
    public ResponseEntity<List<UserResponseDto>> getUserList() {
        List<UserResponseDto> users = userService.findAll();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/findAll/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID userId) {
        UserResponseDto response = userService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestParam("user") CreateUserRequest request,
        @RequestParam(value = "file", required = false) MultipartFile file)
        throws Exception {

        UUID userId = null;
        UserResponseDto responseDto = null;
        if (file != null && !file.isEmpty()) {
            userId = userService.create(request);
        } else {
            CreateBinaryContentRequest binaryContentRequest =
                new CreateBinaryContentRequest(file.getName(),
                    file.getContentType(),
                    file.getBytes());

            userId = userService.create(request, binaryContentRequest);
        }

        UserResponseDto response = userService.findByUserId(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID userId,
        @RequestParam UpdateUserRequest request,
        @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(request.getName(),
            request.getEmail(), request.getPassword());

        if (file != null && !file.isEmpty()) {
            CreateBinaryContentRequest binaryContentRequest = new CreateBinaryContentRequest(
                file.getName(),
                file.getContentType(),
                file.getBytes());

            userService.update(userId, updateUserRequest, binaryContentRequest);
        } else {
            userService.update(userId, updateUserRequest);
        }

        UserResponseDto responseDto = userService.findByUserId(userId);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable UUID userId) {
        userService.remove(userId);

        return ResponseEntity.noContent().build();
    }
}
