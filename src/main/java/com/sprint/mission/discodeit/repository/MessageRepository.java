package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void save(Message message);

    Message findByMessageId(UUID messageId);

    void update(UUID messageId, UpdateMessageRequest request);

    List<Message> findAll();

    List<Message> findAllByChannelId(UUID channelId);

    void delete(UUID id);
}
