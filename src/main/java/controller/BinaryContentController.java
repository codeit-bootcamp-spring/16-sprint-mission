package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 1. 기존 바이너리 파일 1개 다운로드
    @RequestMapping(
            value = "/api/binaryContents/{binaryContentId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<byte[]> download(
            @PathVariable UUID binaryContentId
    ) {

        // 기존 서비스를 이용해 파일 조회
        BinaryContent binaryContent =
                binaryContentService.find(binaryContentId);

        // 파일의 Content-Type 설정
        MediaType mediaType = MediaType.parseMediaType(
                binaryContent.getContentType()
        );

        // 파일 데이터를 HTTP 응답으로 반환
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment"
                )
                .body(binaryContent.getBytes());
    }

    // 2. 기존 바이너리 파일 여러 개 조회
    @RequestMapping(
            value = "/api/binaryContents",
            method = RequestMethod.GET
    )
    public List<BinaryContent> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {

        // 여러 파일을 ID 목록으로 조회
        return binaryContentService.findAllByIdIn(
                binaryContentIds
        );
    }

    // 3. 심화 요구사항: 바이너리 파일 조회
    @RequestMapping(
            value = "/api/binaryContent/find",
            method = RequestMethod.GET
    )
    public ResponseEntity<BinaryContent> find(
            @RequestParam UUID binaryContentId
    ) {

        // 파일 ID로 바이너리 데이터 조회
        BinaryContent binaryContent =
                binaryContentService.find(binaryContentId);

        // 조회한 BinaryContent 객체 반환
        return ResponseEntity.ok(binaryContent);
    }
}
