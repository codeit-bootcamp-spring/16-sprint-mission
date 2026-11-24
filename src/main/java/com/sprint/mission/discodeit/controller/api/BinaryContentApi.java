package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부파일 및 프로필 바이너리 데이터 조회 API")
public interface BinaryContentApi {

    @Operation(summary = "단건 파일 조회", description = "ID로 특정 바이너리 콘텐츠를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 파일을 찾을 수 없음")
    })
    @GetMapping("/{binaryContentId}")
    ResponseEntity<BinaryContent> find(
            @Parameter(description = "조회할 바이너리 콘텐츠 UUID") @PathVariable UUID binaryContentId);

    @Operation(summary = "다건 파일 조회", description = "여러 개의 ID 목록으로 바이너리 콘텐츠 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파일 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터(binaryContentIds) 누락 또는 형식 오류")
    })
    @GetMapping
    ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @Parameter(description = "조회할 바이너리 콘텐츠 UUID 목록") @RequestParam List<UUID> binaryContentIds);
}
