package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "프로필 이미지와 메시지 첨부파일 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @Operation(summary = "바이너리 파일 단건 조회")
    @ApiResponse(responseCode = "200", description = "파일 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "파일 ID의 UUID 형식이 올바르지 않음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "invalidBinaryContentId",
                            value = "Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: abc"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "해당 ID의 파일을 찾을 수 없음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "binaryContentNotFound",
                            value = "BinaryContent with id 88e75d27-b6b7-488a-a19e-51ded3868800 not found"
                    )
            )
    )
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(
            @Parameter(description = "조회할 파일의 UUID")
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    @Operation(summary = "바이너리 파일 다건 조회")
    @ApiResponse(responseCode = "200", description = "파일 목록 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "binaryContentIds가 누락되었거나 UUID 형식이 올바르지 않음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "missingBinaryContentIds",
                            value = "Required request parameter 'binaryContentIds' for method parameter type List is not present"
                    )
            )
    )
    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @Parameter(description = "조회할 파일의 UUID 목록")
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    ) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }
}