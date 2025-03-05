package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    @Override
    public void save(Message user) {

    }

    @Override
    public Message findByMessageId(UUID messageId) {
        return null;
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        return null;
    }

    @Override
    public List<Message> findAll() {
        return List.of();
    }

    @Override
    public void delete(UUID id) {
        return null;
    }

    @Override
    public void clearDb() {

    }
}
