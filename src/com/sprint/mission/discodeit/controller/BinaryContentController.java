package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 여러 첨부파일 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> findAll(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContentDto> binaryContents =
                binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.ok(binaryContents);
    }

    // 첨부파일 단건 조회
    @RequestMapping(
            value = "/{binaryContentId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<BinaryContentDto> findById(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentDto binaryContent = binaryContentService
                .findById(binaryContentId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "첨부파일 정보를 찾을 수 없습니다."
                        )
                );

        return ResponseEntity.ok(binaryContent);
    }
}