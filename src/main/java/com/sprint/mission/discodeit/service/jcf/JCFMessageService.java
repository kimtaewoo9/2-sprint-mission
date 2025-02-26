package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.repository.message.MessageRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;

        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(String content, UUID userId, UUID channelId) {
        // Message 검증 로직
        try{
            userService.findByUserId(userId);
            channelService.findByChannelId(channelId);
        }catch (IllegalArgumentException e){
            throw e;
        }

        Message message = new Message(content, userId, channelId);
        messageRepository.save(message);
    }

    @Override
    public Message readById(UUID messageId){
        return messageRepository.findByMessageId(messageId);
    }

    @Override
    public List<Message> readAll(){
        return messageRepository.findAll();
    }

    @Override
    public Message remove(UUID messageId) {
        return messageRepository.delete(messageId);
    }
}
