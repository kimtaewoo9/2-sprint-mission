package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BinaryContent implements Serializable {

    private final UUID id;
    private final Instant createdAt;

    private byte[] binaryImage;

    // User 생성, 수정 or 메시지 생성 시에 BinaryContent 생성
    public BinaryContent(byte[] binaryImage) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.binaryImage = binaryImage;
    }
}
