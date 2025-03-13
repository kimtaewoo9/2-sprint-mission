package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final Path messageDirectory;

    private FileMessageRepository() {
        this.messageDirectory = FileUtils.getBaseDirectory().resolve("messages");
        FileUtils.init(messageDirectory);
    }

    private static class SingletonHolder{
        private static final FileMessageRepository INSTANCE = new FileMessageRepository();
    }

    public static FileMessageRepository getInstance(){
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void save(Message message) {
        Path messageFile = messageDirectory.resolve(message.getId().toString() + ".message");
        FileUtils.save(messageFile, message);
    }

    @Override
    public Message findByMessageId(UUID messageId) {
        Path messageFile = messageDirectory.resolve(messageId.toString() +".message");
        return Optional.ofNullable((Message)FileUtils.loadById(messageFile))
                .orElseThrow(() -> new IllegalArgumentException("[ERROR]유효 하지 않은 아이디 입니다. id :" + messageId));

    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = findByMessageId(messageId);
        message.updateContent(newContent);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return FileUtils.load(messageDirectory);
    }

    @Override
    public void delete(UUID messageId) {
        Path messageFile = messageDirectory.resolve(messageId.toString()+".message");
        FileUtils.delete(messageFile);
    }

    @Override
    public void clearDb() {
        FileUtils.clearDirectory(messageDirectory);
    }
}
