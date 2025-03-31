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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping
    public ResponseEntity<List<ChannelResponseDto>> getAllChannels() {
        List<ChannelResponseDto> channelList = channelService.findAll();

        return ResponseEntity.ok(channelList);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ChannelResponseDto>> getChannelListByUserId(
        @PathVariable UUID userId) {
        List<ChannelResponseDto> channelList = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channelList);
    }

    @PostMapping("/public")
    public ResponseEntity<ChannelResponseDto> createPublicChannel(
        @RequestBody CreatePublicChannelRequest request
    ) {

        UUID channelId = channelService.createPublicChannel(request);
        ChannelResponseDto response = channelService.findByChannelId(channelId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(
        @RequestBody CreatePrivateChannelRequest request
    ) {

        UUID channelId = channelService.createPrivateChannel(request);
        ChannelResponseDto response = channelService.findByChannelId(channelId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{channelId}")
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
