package com.sprint.mission.discodeit.annotation.swagger.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

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
@Operation(summary = "User별 ReadStatus 목록 조회")
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "ReadStatus 목록 조회 성공",
                content = @Content(
                        array = @ArraySchema(
                                schema = @Schema(implementation = ReadStatus.class)
                        )
                )
        )
})
public @interface ReadStatusFindByUserIdDocs {
}
