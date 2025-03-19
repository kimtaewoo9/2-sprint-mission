package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileReadStatusService implements ReadStatusService {

    private ReadStatusRepository readStatusRepository;
    private UserRepository userRepository;
    private ChannelRepository channelRepository;

    @Override
    public void create(CreateReadStatusRequest request) {
        if (channelRepository.findByChannelId(request.getChannelId()) == null) {
            throw new IllegalArgumentException("[ERROR] channel not found");
        }
        if (userRepository.findByUserId(request.getUserId()) == null) {
            throw new IllegalArgumentException("[ERROR] user not found");
        }
        if (readStatusRepository.isExist(request.getChannelId(),
            request.getUserId()) != null) {
            throw new IllegalArgumentException("[ERROR] read status is already exist");
        }

        ReadStatus readStatus = new ReadStatus(request.getChannelId(), request.getUserId());
        readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.find(readStatusId);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return readStatusRepository.findAllByChannelId(channelId);
    }

    @Override
    public void update(UUID id, UpdateReadStatusRequest request) {
        readStatusRepository.update(id, request.getChannelId(), request.getUserId());
    }

    @Override
    public void delete(UUID readStatusId) {
        readStatusRepository.delete(readStatusId);
    }
}
