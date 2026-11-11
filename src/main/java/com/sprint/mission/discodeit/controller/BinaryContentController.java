package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentListResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binarycontents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @PostMapping
    public ResponseEntity<BinaryContentResponse> createBinaryContent(
            @Valid @RequestBody BinaryContentCreateRequest request) {
        BinaryContentResponse binaryContentResponse = this.binaryContentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(binaryContentResponse);
    }

    @GetMapping("/{binary-content-id}")
    public ResponseEntity<BinaryContentResponse> findBinaryContentById(
            @PathVariable("binary-content-id") UUID binaryContentId) {
        BinaryContentResponse binaryContentResponse = this.binaryContentService.find(
                binaryContentId);

        return ResponseEntity.ok(binaryContentResponse);
    }

    @GetMapping
    public ResponseEntity<BinaryContentListResponse> findBinaryContentsByIds(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentResponse> binaryContentListResponse
                = this.binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.ok(new BinaryContentListResponse(binaryContentListResponse));
    }

    @GetMapping("/{binary-content-id}/download")
    public ResponseEntity<byte[]> downloadBinaryContent(
            @PathVariable("binary-content-id") UUID binaryContentId) {

        BinaryContentResponse response = this.binaryContentService.find(binaryContentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(response.fileName(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .contentLength(response.bytes().length)
                .body(response.bytes());
    }

    @DeleteMapping("/{binary-content-id}")
    public ResponseEntity<Void> deleteBinaryContent(@PathVariable("binary-content-id") UUID binaryContentId) {
        this.binaryContentService.delete(binaryContentId);

        return ResponseEntity.noContent().build();
    }
}
