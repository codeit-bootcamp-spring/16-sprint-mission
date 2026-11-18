package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
public interface BinaryContentApi {

    @Operation(summary = "첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공")
    @ApiResponse(
            responseCode = "404",
            description = "첨부 파일을 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "첨부 파일을 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "바이너리 콘텐츠를 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    ResponseEntity<BinaryContent> findById(
            @PathVariable UUID binaryContentId
    );

    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
    List<BinaryContent> findAllByIds(
            @RequestParam List<UUID> binaryContentIds
    );
}
