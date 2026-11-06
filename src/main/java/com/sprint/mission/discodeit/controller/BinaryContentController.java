package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 1. 바이너리 파일 1개 다운로드
    @RequestMapping(
            value = "/{binaryContentId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<byte[]> download(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        // 저장된 파일의 이름, 타입, 실제 데이터를 조회합니다.
        BinaryContent content = binaryContentService.findById(binaryContentId);

        // 다운로드할 때 사용할 파일 이름을 헤더에 지정합니다.
        // UTF-8로 지정해 한글 파일 이름도 처리합니다.
        ContentDisposition disposition = ContentDisposition
                .attachment()
                .filename(content.getFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                // image/png, text/plain 등 저장된 파일의 종류를 지정합니다.
                .contentType(MediaType.parseMediaType(content.getContentType()))

                // 실제 파일의 크기를 지정합니다.
                .contentLength(content.getSize())

                // 브라우저가 첨부 파일로 다운로드하도록 안내합니다.
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )

                // JSON이 아닌 파일의 실제 바이트를 응답합니다.
                .body(content.getData());
    }

    // 2. 바이너리 파일 여러 개 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam("ids") List<UUID> ids // 여러 파일 ID를 받습니다.
    ) {
        // 기존 서비스는 요청한 ID 중 저장된 파일만 반환합니다.
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);

        // 파일 정보와 데이터를 JSON 배열로 응답합니다.
        return ResponseEntity.ok(contents);
    }
}