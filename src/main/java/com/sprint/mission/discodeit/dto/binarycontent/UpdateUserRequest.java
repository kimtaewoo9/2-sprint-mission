package com.sprint.mission.discodeit.dto.binarycontent;

public record UpdateUserRequest(
    String newName,
    String newEmail,
    String newPassword
) {

}
