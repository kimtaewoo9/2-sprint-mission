package com.sprint.mission.discodeit.service.repository.message;

import com.sprint.mission.discodeit.entity.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MapMessageRepository implements MessageRepository {

    private static final Map<UUID, Message> messageDb = new HashMap<>();
    private static final MapMessageRepository instance = new MapMessageRepository();

    private MapMessageRepository(){}

    public static MapMessageRepository getInstance(){
        return instance;
    }

    @Override
    public void save(Message message) {
        messageDb.put(message.getId(), message);
    }

    @Override
    public Message findByMessageId(UUID messageId) {
        return messageDb.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageDb.values());
    }

    @Override
    public Message delete(UUID messageId) {
        return messageDb.remove(messageId);
    }

    @Override
    public void clearDb() {
        messageDb.clear();
    }
}
