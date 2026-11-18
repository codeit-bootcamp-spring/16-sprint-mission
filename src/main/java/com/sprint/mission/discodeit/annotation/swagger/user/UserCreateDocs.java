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
@Operation(summary = "User 등록")
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "User가 성공적으로 생성됨"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                content = @Content(
                        mediaType = "*/*",
                        examples = @ExampleObject(
                                value = "User with email {email} already exists"
                        )
                )
        )
})
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
                encoding = {
                        @Encoding(
                                name = "userCreateRequest",
                                contentType = MediaType.APPLICATION_JSON_VALUE
                        )
                }
        )
)
public @interface UserCreateDocs {
}
