package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @GetMapping("/{fileId}")
    public ResponseEntity<BinaryContent> getFile(@PathVariable UUID fileId) {
        BinaryContent binaryContent = binaryContentService.find(fileId);
        return ResponseEntity.ok(binaryContent);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContent>> getFilesByIds(@RequestParam List<UUID> ids) {
        List<BinaryContent> files = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(files);
    }
}