package com.sprint.mission.discodeit.annotation.swagger.user;

import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation; // 개별 API의 기능 설명
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 개별 HTTP 응답 코드 설명
import io.swagger.v3.oas.annotations.responses.ApiResponses; // API의 응답 목록 정의

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "User 정보 수정")
@ApiResponses({
        @ApiResponse(
                responseCode = "404", description = "User를 찾을 수 없음",
                content = @Content(
                        mediaType = "*/*",
                        examples = @ExampleObject(
                                value = "User with id {userId} not found"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                content = @Content(
                        mediaType = "*/*",
                        examples = @ExampleObject(
                                value = "user with email {newEmail} already exists"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "200", description = "User 정보가 성공적으로 수정됨"
        )
})
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
                encoding = {
                        @Encoding(
                                name = "userUpdateRequest",
                                contentType = MediaType.APPLICATION_JSON_VALUE
                        )
                }
        )
)
public @interface UserUpdateDocs {
}
