package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    void save(Message message);
    Message read(UUID messageId);
    List<Message> readAll();
    Message remove(UUID messageId);
}
