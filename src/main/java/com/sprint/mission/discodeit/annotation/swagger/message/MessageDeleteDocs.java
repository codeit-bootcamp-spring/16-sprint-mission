package com.sprint.mission.discodeit.annotation.swagger.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Message 삭제"
)
@ApiResponses({
        @ApiResponse(
                responseCode = "204",
                description = "Message가 성공적으로 삭제됨"
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
public @interface MessageDeleteDocs {
}
