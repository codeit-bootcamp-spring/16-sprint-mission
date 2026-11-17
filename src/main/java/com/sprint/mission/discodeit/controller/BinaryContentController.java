package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentListResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(
        name = "BinaryContent",
        description = "바이너리 콘텐츠 생성, 조회, 다운로드 및 삭제 API"
)
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @PostMapping
    @Operation(
            summary = "BinaryContent 생성",
            description = "파일 이름, 크기, 콘텐츠 유형 및 바이너리 데이터를 이용하여 BinaryContent를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "BinaryContent가 성공적으로 생성되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BinaryContentResponse.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": "550e8400-e29b-41d4-a716-446655440003",
                          "createdAt": "2026-11-17T06:00:00Z",
                          "fileName": "sample.txt",
                          "size": 5,
                          "contentType": "text/plain",
                          "bytes": "SGVsbG8="
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "파일 이름, 콘텐츠 유형 또는 바이너리 데이터의 유효성 검증 실패, 잘못된 요청 형식",
                    content = {
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string")
                            ),
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            )
                    }
            )
    })
    public ResponseEntity<BinaryContentResponse> createBinaryContent(
            @Valid @RequestBody BinaryContentCreateRequest request
    ) {
        BinaryContentResponse binaryContentResponse =
                this.binaryContentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(binaryContentResponse);
    }

    @GetMapping("/{binaryContentId}")
    @Operation(
            summary = "BinaryContent 단건 조회",
            description = "BinaryContent ID를 기준으로 파일의 메타데이터와 바이너리 데이터를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "BinaryContent 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BinaryContentResponse.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": "550e8400-e29b-41d4-a716-446655440003",
                          "createdAt": "2026-11-17T06:00:00Z",
                          "fileName": "sample.txt",
                          "size": 5,
                          "contentType": "text/plain",
                          "bytes": "SGVsbG8="
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "binaryContentId의 UUID 형식이 올바르지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 BinaryContent가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<BinaryContentResponse> findBinaryContentsById(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        BinaryContentResponse binaryContentResponse =
                this.binaryContentService.find(binaryContentId);

        return ResponseEntity.ok(binaryContentResponse);
    }

    @GetMapping
    @Operation(
            summary = "BinaryContent 목록 조회",
            description = "여러 BinaryContent ID를 이용하여 해당 파일들의 정보를 조회하고 JSON 배열로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "BinaryContent 목록 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = BinaryContentResponse.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "binaryContentIds가 누락되었거나 UUID 목록의 형식이 올바르지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "요청한 BinaryContent 중 존재하지 않는 파일이 있습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<List<BinaryContentResponse>> findBinaryContentsByIds(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    ) {
        BinaryContentListResponse binaryContentListResponse
                = this.binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.ok(
                binaryContentListResponse.binaryContents()
        );
    }

    @GetMapping("/{binary-content-id}/download")
    @Operation(
            summary = "BinaryContent 다운로드",
            description = "지정한 BinaryContent의 원본 바이너리 데이터를 파일로 다운로드합니다. 응답의 Content-Type은 저장된 파일의 콘텐츠 유형에 따라 결정됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "BinaryContent 다운로드에 성공했습니다.",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(
                                    type = "string",
                                    format = "binary"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "binary-content-id의 UUID 형식이 올바르지 않거나 저장된 콘텐츠 유형이 유효하지 않습니다.",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            ),
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "다운로드할 BinaryContent가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<byte[]> downloadBinaryContent(
            @PathVariable("binary-content-id") UUID binaryContentId
    ) {

        BinaryContentResponse response =
                this.binaryContentService.find(binaryContentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(response.fileName(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .contentLength(response.bytes().length)
                .body(response.bytes());
    }

    @DeleteMapping("/{binary-content-id}")
    @Operation(
            summary = "BinaryContent 삭제",
            description = "지정한 BinaryContent를 저장소에서 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "BinaryContent가 성공적으로 삭제되었습니다. 응답 본문은 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "binary-content-id의 UUID 형식이 올바르지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 BinaryContent가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<Void> deleteBinaryContent(
            @PathVariable("binary-content-id") UUID binaryContentId
    ) {
        this.binaryContentService.delete(binaryContentId);

        return ResponseEntity.noContent().build();
    }
}