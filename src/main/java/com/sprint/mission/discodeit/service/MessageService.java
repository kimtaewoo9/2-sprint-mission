package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageDto create(CreateMessageRequest request);

    MessageDto create(CreateMessageRequest request,
        List<CreateBinaryContentRequest> binaryContents);

    MessageDto findById(UUID messageId);

    List<MessageDto> findAll();

    List<MessageDto> findByChannelId(UUID channelId);

    MessageDto update(UUID messageId, UpdateMessageRequest request);

    void remove(UUID messageId);
}
