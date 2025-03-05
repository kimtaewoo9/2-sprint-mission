package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    void save(Message message);
    Message findByMessageId(UUID messageId);
    Message update(UUID messageId, String newContent);
    List<Message> findAll();
    void delete(UUID id);
    void clearDb();
}
