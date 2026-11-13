package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContent> findById(
            @PathVariable UUID binaryContentId
    ) {
        return ResponseEntity.ok(
                binaryContentService.find(binaryContentId));
    }

    @GetMapping
    public List<BinaryContent> findAllByIds(
            @RequestParam List<UUID> binaryContentIds
    ) {
        return binaryContentService.findAllByIdIn(binaryContentIds);
    }
}


