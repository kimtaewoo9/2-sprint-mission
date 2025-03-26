package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;

@Getter
public class UpdateUserRequest {
    String name;
    String email;
    String password;
    String profileImageId;

    public UpdateUserRequest(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
