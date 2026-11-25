package com.sprint.mission.discodeit.annotation.swagger.channel;

import com.sprint.mission.discodeit.dto.ChannelDto;

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
@Operation(summary = "Public Channel 생성")
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Public Channel이 성공적으로 생성됨",
                content = @Content(
                        schema = @Schema(implementation = ChannelDto.class)
                )
        )
})
public @interface ChannelPublicCreateDocs {
}
