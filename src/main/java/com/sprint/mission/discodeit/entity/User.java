package com.sprint.mission.discodeit.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    // createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언합니다.
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private List<Message> messageList = new ArrayList<>();

    public User(String name) {
        this.id = UUID.randomUUID(); // id는 생성자에서 초기화하세요.
        this.createdAt = System.currentTimeMillis(); // createdAt는 생성자에서 초기화하세요.
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
    }

    // 각 필드를 반환하는 Getter 함수를 정의하세요.
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String name){
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getName() {
        return name;
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
