package com.sprint.mission.discodeit.annotation.swagger.message;

import com.sprint.mission.discodeit.dto.MessageDto;

import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Message 생성")
@ApiResponses({
        @ApiResponse(
                responseCode = "404",
                description = "Channel 또는 User를 찾을 수 없음",
                content = @Content(
                        mediaType = "*/*",
                        examples = @ExampleObject(
                                value = "Channel | Author with id {channelId | authorId} not found"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "201",
                description = "Message가 성공적으로 생성됨",
                content = @Content(
                        mediaType = "*/*",
                        schema = @Schema(implementation = MessageDto.class)
                )
        )
})
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
                encoding = {
                        @Encoding(
                                name = "messageCreateRequest",
                                contentType = MediaType.APPLICATION_JSON_VALUE
                        )
                }
        )
)
public @interface MessageCreateDocs {
}
