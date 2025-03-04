package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {

    // 각 필드를 반환하는 Getter 함수를 정의하세요.
    @Getter
    private final UUID id;
    @Getter
    private final Long createdAt;
    private Long updatedAt;
    @Getter
    private String name;
    private List<Message> messageList = new ArrayList<>();

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

    public List<Message> getMessageList(){
        return new ArrayList<>(messageList);
    }

    @Override
    public String toString() {
        return "User{" +
                "createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userName='" + name + '\'' +
                '}';
    }
}
