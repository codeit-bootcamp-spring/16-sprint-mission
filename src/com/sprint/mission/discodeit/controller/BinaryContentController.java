package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 여러 첨부파일 조회
    @ApiResponse(responseCode = "200", description = "첨부파일 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> findAll(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContentDto> binaryContents =
                binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.ok(binaryContents);
    }

    // 첨부파일 단건 조회
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "첨부파일 조회 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부파일 정보를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
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