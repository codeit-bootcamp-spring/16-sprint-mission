package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentResponse> getContent(@PathVariable("binaryContentId") UUID contentId) {
        BinaryContentResponse response = binaryContentService.find(contentId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentResponse>> getContents(@RequestParam("binaryContentIds") List<UUID> contentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(contentIds));
    }

    // 저장된 형식이 없거나 잘못된 경우 일반 바이너리 파일로 응답
    private MediaType toMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (InvalidMediaTypeException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
