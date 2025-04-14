package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "binary_contents")
@NoArgsConstructor
@Getter
@Setter
public class BinaryContent extends BaseEntity implements Persistable<UUID> {

    private String fileName;

    private Long size;

    private String ContentType;

    @Lob
    private byte[] bytes;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PrePersist
    public void martNotNew() {
        this.isNew = false;
    }
}
