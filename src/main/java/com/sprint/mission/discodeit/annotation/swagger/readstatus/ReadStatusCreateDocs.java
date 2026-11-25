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
@Operation(summary = "ReadStatus 생성")
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "ReadStatus가 성공적으로 생성됨",
                content = @Content(
                        schema = @Schema(implementation = ReadStatus.class)
                )
        )
})
public @interface ReadStatusCreateDocs {
}
