package com.sprint.mission.discodeit.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Channel {

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;

    private final Map<UUID, User> users; // 채널에 속한 유저 .
    private final Map<UUID, Message> messages;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;

        this.users = new HashMap<>();
        this.messages = new HashMap<>();
    }

    public void update(String name){
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public void join(User user){
        users.put(user.getId(), user);
    }

    public void leave(User user){
        users.remove(user.getId());
    }

    public void addMessage(Message message){
        messages.put(message.getId(), message);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + name + '\'' +
                '}';
    }
}
