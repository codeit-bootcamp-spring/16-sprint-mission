package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDownloadDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 첨부파일 원본 조회
    @RequestMapping(
            value = "/find",
            method = RequestMethod.GET
    )
    public ResponseEntity<BinaryContent> find(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContent binaryContent = binaryContentService
                .findEntityById(binaryContentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "첨부파일 정보를 찾을 수 없습니다."
                        )
                );

        return ResponseEntity.ok(binaryContent);
    }

    // 파일 하나 다운로드
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<ByteArrayResource> download(
            @PathVariable UUID id
    ) {
        BinaryContentDownloadDto file = binaryContentService.downloadById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("첨부파일을 찾을 수 없습니다.")
                );

        ByteArrayResource resource = new ByteArrayResource(file.bytes());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.bytes().length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" +
                                java.net.URLEncoder.encode(
                                        file.fileName(),
                                        StandardCharsets.UTF_8
                                ).replace("+", "%20")
                )
                .body(resource);
    }

    // 여러 파일 ZIP 다운로드
    @RequestMapping(
            value = "/download",
            method = RequestMethod.GET
    )
    public ResponseEntity<ByteArrayResource> downloadAll(
            @RequestParam List<UUID> ids
    ) throws IOException {

        List<BinaryContentDownloadDto> files =
                binaryContentService.downloadAllByIdIn(ids);

        if (files.isEmpty()) {
            throw new IllegalArgumentException("다운로드할 파일이 없습니다.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutputStream =
                     new ZipOutputStream(outputStream)) {

            for (BinaryContentDownloadDto file : files) {
                ZipEntry entry = new ZipEntry(file.fileName());

                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(file.bytes());
                zipOutputStream.closeEntry();
            }
        }

        ByteArrayResource resource =
                new ByteArrayResource(outputStream.toByteArray());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"attachments.zip\""
                )
                .contentLength(outputStream.size())
                .body(resource);
    }
}