package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface ReadStatusService {

    UUID create(CreateReadStatusRequest request);

    ReadStatusResponseDto find(UUID readStatusId);

    List<ReadStatusResponseDto> findAllByUserId(UUID userId);

    List<ReadStatusResponseDto> findAllByChannelId(UUID channelId);

    void update(UUID id, UpdateReadStatusRequest request);

    void updateByChannelId(UUID channelId, UpdateReadStatusRequest request);

    void delete(UUID readStatusId);
}
