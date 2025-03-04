package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private static final Map<UUID, Message> messageDb = new HashMap<>();

    private static class SingletonHolder{
        private static final JCFMessageRepository INSTANCE = new JCFMessageRepository();
    }

    private JCFMessageRepository(){}

    public static JCFMessageRepository getInstance(){
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void save(Message message) {
        messageDb.put(message.getId(), message);
    }

    @Override
    public Message findByMessageId(UUID messageId) {
        // 유효 하지 않은 id를 입력 했을때 오류를 출력하고 다시 입력 받기
        return Optional.ofNullable(messageDb.get(messageId))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 아이디 입니다. id : " + messageId));
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message messageNullable = messageDb.get(messageId);
        Message message = Optional.ofNullable(messageNullable).
                orElseThrow(() -> new NoSuchElementException("Message ID Error"));
        message.update(newContent);

        return message;
    }

    @Override
    public List<Message> findAll() {
        // 목록이 없으면 빈 콜렉션 반환.
        return Collections.unmodifiableList(new ArrayList<>(messageDb.values()));
    }

    @Override
    public Message delete(UUID messageId) {
        if(!messageDb.containsKey(messageId)){
            throw new NoSuchElementException("Message ID Error");
        }

        return messageDb.remove(messageId);
    }

    @Override
    public void clearDb() {
        messageDb.clear();
    }
}
