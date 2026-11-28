package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.annotation.swagger.binarycontent.BinaryContentDownloadDocs;
import com.sprint.mission.discodeit.annotation.swagger.binarycontent.BinaryContentListDocs;
import com.sprint.mission.discodeit.annotation.swagger.binarycontent.BinaryContentReadDocs;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @BinaryContentReadDocs
    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable UUID binaryContentId) {

        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto response = new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType()
        );

        return ResponseEntity.ok(response);
    }

    @BinaryContentListDocs
    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }

    @BinaryContentDownloadDocs
    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<byte[]> download(
            @Parameter(
                    description = "다운로드할 파일 ID",
                    required = true
            )
            @PathVariable UUID binaryContentId) {

        BinaryContent binaryContent =
                binaryContentService.find(binaryContentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        binaryContent.getContentType()
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(binaryContent.getFileName(),
                                        StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(binaryContent.getBytes());
    }
}