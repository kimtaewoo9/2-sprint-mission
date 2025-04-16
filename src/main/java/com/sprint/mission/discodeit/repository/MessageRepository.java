package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByChannelId(UUID channelId);

    @Modifying
    @Query("DELETE FROM Message as m WHERE m.channel.id = :channelId")
    void deleteByChannelId(UUID channelId);
}
