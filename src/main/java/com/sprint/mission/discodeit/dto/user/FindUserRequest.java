package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindUserRequest {

    private UUID id;
    String name;
    String email;
    boolean isOnline;
    UUID profileImageId;

    public static FindUserRequest from(User user, boolean isOnline){
        return new FindUserRequest(
            user.getId(),
            user.getName(),
            user.getEmail(),
            isOnline,
            user.getProfileImageId()
        );
    }
}
