package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<ChannelResponseDto>> getChannelListByUserId(
        @PathVariable UUID userId) {
        List<ChannelResponseDto> channelResponseDtos = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channelResponseDtos);
    }

    @PostMapping("/public")
    public ResponseEntity<ChannelResponseDto> create(
        @RequestBody CreatePublicChannelRequest request
    ) {

        UUID channelId = channelService.createPublicChannel(request);
        ChannelResponseDto response = channelService.findByChannelId(channelId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponseDto> create(
        @RequestBody CreatePrivateChannelRequest request
    ) {

        UUID channelId = channelService.createPrivateChannel(request);
        ChannelResponseDto response = channelService.findByChannelId(channelId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponseDto> update(
        @PathVariable UUID channelId,
        @RequestBody UpdateChannelRequest request) {

        channelService.update(channelId, request);
        ChannelResponseDto response = channelService.findByChannelId(channelId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID channelId
    ) {
        channelService.remove(channelId);

        return ResponseEntity.noContent().build();
    }
}
