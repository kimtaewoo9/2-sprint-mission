package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    void create(CreateMessageRequest request);

    Message findById(UUID messageId);

    List<MessageResponseDto> findAll();

    List<MessageResponseDto> findByChannelId(UUID channelId);

    void update(UUID messageId, UpdateMessageRequest request);

    void remove(UUID messageId);
}
