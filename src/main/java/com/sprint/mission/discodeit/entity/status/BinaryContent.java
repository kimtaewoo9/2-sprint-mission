package com.sprint.mission.discodeit.entity.status;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BinaryContent {

    private UUID id;
    private Instant createdAt;
    private byte[] binaryImage;

    // User 생성, 수정 or 메시지 생성 시에 BinaryContent 생성
    public BinaryContent(byte[] binaryImage, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.createdAt = createdAt;
        this.binaryImage = binaryImage;
    }
}
