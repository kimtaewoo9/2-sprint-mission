package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BinaryContentService binaryContentService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUserList() {
        List<UserResponseDto> responseDtos = userService.findAll();

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID userId) {
        UserResponseDto response = userService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestPart(value = "file", required = false)
    MultipartFile file, @RequestPart("user") CreateUserRequest request) throws Exception {

        UUID profileId = null;
        if (file != null && !file.isEmpty()) {
            profileId = binaryContentService.create(
                new CreateBinaryContentRequest(file.getBytes()));
        }

        UUID userId = userService.create(request, profileId);
        UserResponseDto response = userService.findByUserId(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID userId,
        @RequestPart UpdateUserRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        UUID updatedUserId = null;
        UUID binaryContentId = null;

        if (file != null && !file.isEmpty()) {
            binaryContentId = binaryContentService.create(
                new CreateBinaryContentRequest(file.getBytes()));
        }
        updatedUserId = userService.update(userId, request, binaryContentId);

        UserResponseDto responseDto = userService.findByUserId(updatedUserId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable UUID userId) {
        userService.remove(userId);
        return ResponseEntity.noContent().build();
    }
}
