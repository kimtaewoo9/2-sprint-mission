package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void save(Message user);
    Message findByMessageId(UUID messageId);
    List<Message> findAll();
    Message delete(UUID id);
    void clearDb();
}
