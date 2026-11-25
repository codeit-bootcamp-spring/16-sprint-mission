package com.sprint.mission.discodeit.annotation.swagger.user;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation; // 개별 API의 기능 설명
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 개별 HTTP 응답 코드 설명
import io.swagger.v3.oas.annotations.responses.ApiResponses; // API의 응답 목록 정의

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "User 삭제")
@ApiResponses({
        @ApiResponse(
                responseCode = "204", description = "User가 성공적으로 삭제됨"
        ),
        @ApiResponse(
                responseCode = "404", description = "User를 찾을 수 없음",
                content = @Content(
                        mediaType = "*/*",
                        examples = @ExampleObject(
                                value = "User with id {userId} not found"
                        )
                )
        )
})
public @interface UserDeleteDocs {
}
