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
    
    public BinaryContent(byte[] binaryImage) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.binaryImage = binaryImage;
    }
}
