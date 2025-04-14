package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "user_statuses")
@NoArgsConstructor
@Getter
public class UserStatus extends BaseUpdatableEntity implements Persistable<UUID> {

    @OneToOne(mappedBy = "userStatus")
    private User user;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return false;
    }
}
