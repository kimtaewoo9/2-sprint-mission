package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String content;

    private final UUID senderId;
    private final UUID channelId;

    public Message(String content, UUID senderId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    public void update(String newContent){
        boolean anyValueUpdated = false;
        if(newContent != null && !newContent.equals(this.content)){
            this.content = newContent;
            anyValueUpdated = true;
        }

        if(anyValueUpdated){
            this.updatedAt = System.currentTimeMillis();
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", sender='" + senderId + '\'' +
                '}';
    }
}
