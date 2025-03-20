package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import lombok.Data;

@Data
public class UserResponseDto {

    private UUID id;
    private String name;
    private String email;
    private UUID profileId;
    private boolean isOnline;

    private UserResponseDto(UUID id, String name, String email, UUID profileId, boolean isOnline) {
    }

    public static UserResponseDto from(User user, boolean isOnline) {
        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getProfileImageId(),
            isOnline
        );
    }
}
