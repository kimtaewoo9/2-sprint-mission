package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JCFMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    @Override
    public void create(String content, UUID userId, UUID channelId) {
        validateContent(content);
        validateUserIdAndChannelId(userId, channelId);

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
    public Message update(UUID messageId, String content) {
        Message message = readById(messageId);
        message.updateContent(content);
        return message;
    }

    @Override
    public void remove(UUID messageId) {
        messageRepository.delete(messageId);
    }

    private void validateUserIdAndChannelId(UUID userId, UUID channelId) {
        try{
            userService.findByUserId(userId);
            channelService.findByChannelId(channelId);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("사용자 정보 또는 채널 정보를 확인할 수 없습니다.");
        }
    }

    private void validateContent(String content) {
        if(content == null || content.isEmpty()){
            throw new IllegalArgumentException("메시지 내용이 없습니다. 메시지를 입력해주세요.");
        }
    }
}
