package com.sprint.mission.discodeit.annotation.swagger.user;

import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation; // 개별 API의 기능 설명
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 개별 HTTP 응답 코드 설명
import io.swagger.v3.oas.annotations.responses.ApiResponses; // API의 응답 목록 정의

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "User 온라인 상태 업데이트")
@ApiResponses({
        @ApiResponse(
                responseCode = "404",
                description = "해당 User의 UserStatus를 찾을 수 없음",
                content = @Content(
                        mediaType = "*/*",
                        examples = @ExampleObject(
                                value = "UserStatus with userId {userId} not found"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "200",
                description = "User 온라인 상태가 성공적으로 업데이트됨",
                content = @Content(
                        mediaType = "*/*",
                        schema = @Schema(implementation = UserStatusDto.class)
                )
        )
})
public @interface UserStatusUpdateDocs {
}
