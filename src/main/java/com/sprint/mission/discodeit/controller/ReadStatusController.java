package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponseDto> create(
        @RequestBody CreateReadStatusRequest request) {

        UUID readStatusId = readStatusService.create(request);
        ReadStatusResponseDto response = readStatusService.find(readStatusId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{readStatusId}")
    public ResponseEntity<Void> updateReadStatus(
        @PathVariable UUID readStatusId,
        @RequestBody UpdateReadStatusRequest request
    ) {
        readStatusService.update(readStatusId, request);

        return ResponseEntity.ok().build();
    }


    @PatchMapping("/channels/{channelId}")
    public ResponseEntity<Void> updateReadStatusByChannelId(
        @PathVariable UUID channelId,
        @RequestBody UpdateReadStatusRequest request) {

        readStatusService.updateByChannelId(channelId, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ReadStatusResponseDto>> getReadStatusByUserId(
        @PathVariable UUID userId
    ) {
        List<ReadStatusResponseDto> readStatuses = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(readStatuses);
    }
}
