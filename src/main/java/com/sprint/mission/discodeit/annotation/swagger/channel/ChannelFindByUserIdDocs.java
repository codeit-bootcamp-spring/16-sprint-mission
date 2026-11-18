package com.sprint.mission.discodeit.annotation.swagger.channel;

import com.sprint.mission.discodeit.dto.ChannelDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@Operation(summary = "User가 참여 중인 Channel 목록 조회")
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Channel 목록 조회 성공",
                content = @Content(
                        array = @ArraySchema(
                                schema = @Schema(implementation = ChannelDto.class)
                        )
                )
        )
})
public @interface ChannelFindByUserIdDocs {
}
