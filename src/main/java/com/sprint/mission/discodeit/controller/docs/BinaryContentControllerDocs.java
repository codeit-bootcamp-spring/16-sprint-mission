package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일")
public interface BinaryContentControllerDocs {
    @Operation(summary = "첨부 파일 다수 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 다수 조회 성공"
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<List<BinaryContentResponse>> findAllFilesByIds(@RequestParam("binaryContentIds") @NotNull List<@NotNull UUID> binaryContentIds);

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
    ResponseEntity<BinaryContentResponse> findById(@Parameter(description = "첨부 파일 ID") @NotNull @PathVariable UUID binaryContentId);
}
