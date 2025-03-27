package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private final List<UUID> userIds;

    private Instant updatedAt;
    private String name;
    private String description;
    private ChannelType type;

    public Channel(String name, String description, ChannelType type) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.name = name;
        this.description = description;
        this.type = type;

        this.userIds = new ArrayList<>();
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        this.name = description;
        this.updatedAt = Instant.now();
    }

    public void updateChannelType(ChannelType type) {
        this.type = type;
        this.updatedAt = Instant.now();
    }

    public void addUser(UUID userId) {
        userIds.add(userId);
    }
}
