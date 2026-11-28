package com.sprint.mission.discodeit.annotation.swagger.binarycontent;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "여러 첨부 파일 조회")
@Parameter(
        name = "binaryContentIds",
        description = "조회할 첨부 파일 ID 목록",
        required = true,
        in = ParameterIn.QUERY,
        array = @ArraySchema(
                schema = @Schema(type = "string", format = "uuid")
        )
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "첨부 파일 목록 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(
                                schema = @Schema(implementation = BinaryContentDto.class)
                        )
                )
        )
})
public @interface BinaryContentListDocs {
}
