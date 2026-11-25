package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BinaryContentQueryController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/api/binaryContent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(
            @RequestParam("binaryContentId") UUID binaryContentId
    ) {
        BinaryContent content = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok(content);
    }

    @GetMapping("/api/binaryContents/{binaryContentId}")
    public BinaryContentResponse findById(@PathVariable("binaryContentId") UUID binaryContentId) {
        return new BinaryContentResponse(binaryContentService.findById(binaryContentId));
    }

    @GetMapping("/api/binaryContents")
    public List<BinaryContentResponse> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    ) {
        return binaryContentService.findAllByIdIn(binaryContentIds).stream()
                .map(BinaryContentResponse::new)
                .toList();
    }

    public record BinaryContentResponse(
            UUID id,
            Instant createdAt,
            String fileName,
            long size,
            String contentType,
            byte[] bytes
    ) {
        public BinaryContentResponse(BinaryContent content) {
            this(content.getId(), content.getCreatedAt(), content.getFileName(),
                    content.getSize(), content.getContentType(), content.getData());
        }
    }
}