package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("/api/binaryContents/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> getBinaryContent(
        @PathVariable UUID binaryContentId) {

        BinaryContentDto response = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok(response);
    }
}
