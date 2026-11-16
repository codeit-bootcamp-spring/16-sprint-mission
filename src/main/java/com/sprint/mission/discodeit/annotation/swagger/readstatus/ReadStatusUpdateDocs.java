package com.sprint.mission.discodeit.annotation.swagger.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "ReadStatus 수정")
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "ReadStatus가 성공적으로 수정됨",
                content = @Content(
                        schema = @Schema(implementation = ReadStatus.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 ReadStatus 수정 요청",
                content = @Content(
                        mediaType = "text/plain",
                        schema = @Schema(implementation = String.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "ReadStatus를 찾을 수 없음",
                content = @Content(
                        mediaType = "text/plain",
                        schema = @Schema(implementation = String.class)
                )
        )
})
public @interface ReadStatusUpdateDocs {
}
