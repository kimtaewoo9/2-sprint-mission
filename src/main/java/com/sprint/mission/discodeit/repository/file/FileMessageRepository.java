package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class FileMessageRepository implements MessageRepository {

    private final Path messageDirectory;

    public FileMessageRepository() {
        this.messageDirectory = FileUtils.baseDirectory.resolve("messages");
        FileUtils.init(messageDirectory);
    }

    @Override
    public void save(Message message) {
        Path messageFile = messageDirectory.resolve(message.getId().toString() + ".message");
        FileUtils.save(messageFile, message);
    }

    @Override
    public Message findByMessageId(UUID messageId) {
        Path messageFile = messageDirectory.resolve(messageId.toString() + ".message");
        return Optional.ofNullable((Message) FileUtils.loadById(messageFile))
            .orElseThrow(
                () -> new IllegalArgumentException("[ERROR]유효 하지 않은 아이디 입니다. id :" + messageId));

    }

    @Override
    public void update(UUID messageId, UpdateMessageRequest request) {
        Message message = findByMessageId(messageId);
        message.updateContent(request.getContent());
    }

    @Override
    public List<Message> findAll() {
        return FileUtils.load(messageDirectory);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
            .filter(m -> m.getChannelId().equals(channelId)).toList();
    }

    @Override
    public void delete(UUID messageId) {
        Path messageFile = messageDirectory.resolve(messageId.toString() + ".message");
        FileUtils.delete(messageFile);
    }
}
