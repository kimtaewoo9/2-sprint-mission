package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;

@Getter
public class UpdateUserRequest {

    String username;
    String email;
    String password;

    public UpdateUserRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
