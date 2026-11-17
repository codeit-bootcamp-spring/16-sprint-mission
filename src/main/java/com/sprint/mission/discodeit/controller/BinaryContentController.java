package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "파일", description = "파일 정보 조회 및 다운로드 API")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @Operation(summary = "파일 다운로드", description = "파일 ID로 조회한 파일을 원본 이름으로 다운로드")
    @RequestMapping(method = RequestMethod.GET, value = "/binary-contents/{binaryContentId}")
    public ResponseEntity<byte[]> getBinaryContent(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.findById(binaryContentId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename(binaryContent.getFileName(), StandardCharsets.UTF_8)
                .build().toString()).body(binaryContent.getBytes());
    }

    @Operation(summary = "여러 파일 정보 조회", description = "요청한 파일 ID 중 저장된 파일 정보만 목록으로 반환")
    @RequestMapping(method = RequestMethod.GET, value = "/binaryContents")
    public ResponseEntity<List<BinaryContent>> getAllBinaryContents(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    @Operation(summary = "단일 파일 정보 조회", description = "파일 ID로 조회한 파일 이름과 콘텐츠 유형과 파일 데이터를 JSON으로 반환")
    @RequestMapping(method = RequestMethod.GET, value = "/binaryContents/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(@PathVariable("binaryContentId") UUID binaryContentId) {
        return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
    }
}