package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Binary Content", description = "첨부파일 정보 조회 API")
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 여러 첨부파일 조회
    @Operation(
            summary = "첨부파일 목록 조회",
            description = "전달한 첨부파일 ID 목록에 해당하는 첨부파일 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부파일 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "첨부파일 ID 형식이 올바르지 않음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> findAll(
            @Parameter(
                    description = "조회할 첨부파일 ID 목록 (UUID). 여러 ID를 전달할 수 있습니다.",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContentDto> binaryContents =
                binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.ok(binaryContents);
    }

    // 첨부파일 단건 조회
    @Operation(
            summary = "첨부파일 단건 조회",
            description = "지정한 첨부파일 ID에 해당하는 첨부파일 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부파일 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = BinaryContentDto.class)
                    )
            ),
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
            @Parameter(
                    description = "조회할 첨부파일 ID (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
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