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

    @RequestMapping(
            value = "/{binaryContentId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<byte[]> download(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        BinaryContent content = binaryContentService.findById(binaryContentId);

        // UTF-8로 지정해 한글 파일 이름도 처리합니다.
        ContentDisposition disposition = ContentDisposition
                .attachment()
                .filename(content.getFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.getContentType()))

                .contentLength(content.getSize())

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )

                .body(content.getData());
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam("ids") List<UUID> ids
    ) {
        // 기존 서비스는 요청한 ID 중 저장된 파일만 반환합니다.
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);

        return ResponseEntity.ok(contents);
    }
}