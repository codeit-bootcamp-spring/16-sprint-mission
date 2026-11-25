package com.sprint.mission.discodeit.annotation.swagger.message;

import com.sprint.mission.discodeit.dto.MessageDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Channel의 Message 목록 조회"
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Message 목록 조회 성공",
                content = @Content(
                        mediaType = "*/*",
                        array = @ArraySchema(
                                schema = @Schema(implementation = MessageDto.class)
                        )
                )
        )
})
public @interface MessageReadDocs {
}
