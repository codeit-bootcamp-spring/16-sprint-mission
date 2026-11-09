package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("{content-id}")
    public ResponseEntity<BinaryContentResponse> getContent(@PathVariable("content-id") UUID contentId) {
        BinaryContentResponse response = binaryContentService.find(contentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentResponse>> getContents(@RequestParam List<UUID> contentIds) {
        List<BinaryContentResponse> response = binaryContentService.findAllByIdIn(contentIds);
        return ResponseEntity.ok(response);
    }
}
