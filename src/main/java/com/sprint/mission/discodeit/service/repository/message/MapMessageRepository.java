package com.sprint.mission.discodeit.service.repository.message;

import com.sprint.mission.discodeit.entity.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MapMessageRepository implements MessageRepository {

    private static final Map<UUID, Message> messageDb = new HashMap<>();
    private volatile static MapMessageRepository instance;

    private MapMessageRepository(){}

    public static MapMessageRepository getInstance(){
        // 인스턴스 존재 여부 확인.
        if(instance == null){
            // null이면 락걸고 다시 확인
            synchronized (MapMessageRepository.class){
                if(instance == null){
                    instance = new MapMessageRepository();
                }
            }

        }
        return instance; // 인스턴스 존재하면 반환함
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
    public List<Message> findAll() {
        // 목록이 없으면 빈 콜렉션 반환.
        return Collections.unmodifiableList(new ArrayList<>(messageDb.values()));
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
