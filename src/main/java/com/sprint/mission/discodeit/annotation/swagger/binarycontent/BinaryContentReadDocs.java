package com.sprint.mission.discodeit.annotation.swagger.binarycontent;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "첨부 파일 조회")
@Parameter(
        name = "binaryContentId",
        description = "조회할 첨부 파일 ID",
        required = true,
        in = ParameterIn.PATH,
        schema = @Schema(type = "string", format = "uuid")
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "첨부 파일 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BinaryContentDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "첨부 파일 찾을 수 없음",
                content = @Content(
                        mediaType = "*/*",
                        examples = @ExampleObject(
                                value = "BinaryContent with id {binaryContentId} not found"
                        )
                )
        )
})
public @interface BinaryContentReadDocs {
}
