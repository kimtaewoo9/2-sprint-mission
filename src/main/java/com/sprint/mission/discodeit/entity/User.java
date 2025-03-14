package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.status.UserStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String name;
    private UserStatus status;
    private final List<Message> messageList = new ArrayList<>();

    public User(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.name = name;
    }

    public void update(String name){
        this.name = name;
        this.updatedAt = Instant.now();
    }
}
