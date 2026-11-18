package com.sprint.mission.discodeit.annotation.swagger.channel;

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
@Operation(summary = "Channel 삭제")
@ApiResponses({
        @ApiResponse(
                responseCode = "204",
                description = "Channel이 성공적으로 삭제됨"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Channel을 찾을 수 없음",
                content = @Content(
                        mediaType = "text/plain",
                        schema = @Schema(implementation = String.class)
                )
        )
})
public @interface ChannelDeleteDocs {
}
