package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public FileMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void create(String content, UUID userId, UUID channelId) {
        Message message = new Message(content, userId, channelId);
        messageRepository.save(message);
    }

    @Override
    public Message readById(UUID messageId) {
        return messageRepository.findByMessageId(messageId);
    }

    @Override
    public List<Message> readAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message modify(UUID messageId, String content) {
        Message message = readById(messageId);
        message.update(content);
        return message;
    }

    @Override
    public void remove(UUID messageId) {
        messageRepository.delete(messageId);
    }
}
