package com.sprint.mission.discodeit.annotation.swagger.message;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messages.MessageUpdateRequest;

import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Message 내용 수정"
)
@RequestBody(
        required = true,
        description = "",
        content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = MessageUpdateRequest.class)
        )
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Message가 성공적으로 수정됨",
                content = @Content(
                        mediaType = "*/*",
                        schema = @Schema(implementation = MessageDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Message를 찾을 수 없음",
                content = @Content(
                        mediaType = "*/*",
                        examples = @ExampleObject(
                                value = "Message with id {messageId} not found"
                        )
                )

        )
})
public @interface MessageUpdateDocs {
}
