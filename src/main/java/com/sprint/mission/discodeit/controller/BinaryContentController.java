package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "파일 조회")
@RestController
@AllArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @Operation(summary = "단건 조회", description = "id로 파일 단건 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 파일 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentResponse> getContent(@PathVariable("binaryContentId") UUID contentId) {
        BinaryContentResponse response = binaryContentService.find(contentId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "다건 조회", description = "id 리스트를 통해 다건 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 파일 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public ResponseEntity<List<BinaryContentResponse>> getContents(@RequestParam("binaryContentIds") List<UUID> contentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(contentIds));
    }

    // 저장된 형식이 없거나 잘못된 경우 일반 바이너리 파일로 응답
    private MediaType toMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (InvalidMediaTypeException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
