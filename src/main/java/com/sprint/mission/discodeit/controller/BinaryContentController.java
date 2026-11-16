package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.basic.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/binaryContents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @Operation(summary = "첨부 파일 다수 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 다수 조회 성공"
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> findAllFilesByIds(@RequestParam("binaryContentIds") @NotNull List<@NotNull UUID> binaryContentIds) {
        return ResponseEntity.status(HttpStatus.OK).body(this.binaryContentService.findAllByIdIn(binaryContentIds));
    }

    @Operation(summary = "첨부 파일 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> findById(@Parameter(description = "첨부 파일 ID") @NotNull @PathVariable UUID binaryContentId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.binaryContentService.findById(binaryContentId));
    }
}
