package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.service.MessageService;
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
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/findAll")
    public ResponseEntity<List<MessageResponseDto>> getMessages(@PathVariable UUID channelId) {
        List<MessageResponseDto> messageResponseDtos = messageService.findByChannelId(channelId);

        return ResponseEntity.ok(messageResponseDtos);
    }

    @PostMapping
    public ResponseEntity<MessageResponseDto> create(
        @RequestBody CreateMessageRequest request) {

        UUID messageId = messageService.create(request);
        MessageResponseDto response = messageService.findById(messageId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> update(
        @PathVariable UUID messageId,
        @RequestBody UpdateMessageRequest request) {

        messageService.update(messageId, request);
        MessageResponseDto response = messageService.findById(messageId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.remove(messageId);

        return ResponseEntity.noContent().build();
    }
}
