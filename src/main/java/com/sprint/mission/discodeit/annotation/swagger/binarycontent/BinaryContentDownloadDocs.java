package com.sprint.mission.discodeit.annotation.swagger.binarycontent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "파일 다운로드")
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "파일 다운로드 성공",
                content = @Content(
                        schema = @Schema(type = "string", format = "binary")
                )
        )
})
public @interface BinaryContentDownloadDocs {
}
