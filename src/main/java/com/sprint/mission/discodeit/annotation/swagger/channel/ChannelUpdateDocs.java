package com.sprint.mission.discodeit.annotation.swagger.channel;

import com.sprint.mission.discodeit.dto.ChannelDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Channel 정보 수정")
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Channel 정보가 성공적으로 수정됨",
                content = @Content(
                        schema = @Schema(implementation = ChannelDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Private Channel은 수정할 수 없거나 Channel 이름이 중복됨",
                content = @Content(
                        mediaType = "text/plain",
                        schema = @Schema(implementation = String.class),
                        examples = {
                                @ExampleObject(
                                        name = "Private Channel 수정 시도",
                                        value = "PRIVATE 채널은 수정할 수 없습니다."
                                ),
                                @ExampleObject(
                                        name = "중복된 채널 이름",
                                        value = "중복된 채널 이름입니다."
                                )
                        }
                )
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
public @interface ChannelUpdateDocs {
}
