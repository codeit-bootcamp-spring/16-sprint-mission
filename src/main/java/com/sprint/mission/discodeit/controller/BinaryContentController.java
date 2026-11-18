package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import com.sprint.mission.discodeit.service.BinaryContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentService binaryContentService;

    @Operation(summary = "여러 첨부 파일 조회")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam("binaryContentIds")
            List<UUID> binaryContentIds
    ) {
        if (binaryContentIds == null
                || binaryContentIds.isEmpty()
                || binaryContentIds.contains(null)) {
            throw new IllegalArgumentException(
                    "유효한 binaryContentIds가 필요합니다."
            );
        }

        List<BinaryContent> contents =
                binaryContentRepository.findAllByIdIn(
                        binaryContentIds
                );

        return ResponseEntity.ok(contents);
    }

    @Operation(summary = "첨부 파일 조회")
    @RequestMapping(
            value = "/{binaryContentId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<BinaryContent> find(
            @PathVariable("binaryContentId")
            UUID binaryContentId
    ) {
        BinaryContent content =
                binaryContentService.findEntityById(
                        binaryContentId
                );

        return ResponseEntity.ok(content);
    }
}