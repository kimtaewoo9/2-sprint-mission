package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String email;
    private UUID profileId;
    private boolean isOnline;

    public static UserResponseDto from(User user, boolean isOnline) {
        return new UserResponseDto(
            user.getId(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getName(),
            user.getEmail(),
            user.getProfileImageId(),
            isOnline
        );
    }
}
