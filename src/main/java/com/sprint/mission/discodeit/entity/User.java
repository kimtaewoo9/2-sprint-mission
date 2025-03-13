package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @ToString
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    @Setter
    private UserStatus status;
    private final List<Message> messageList = new ArrayList<>();

    public User(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
    }

    public void update(String name){
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}
