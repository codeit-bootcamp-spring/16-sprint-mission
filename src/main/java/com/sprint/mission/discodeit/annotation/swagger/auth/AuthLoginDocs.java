package com.sprint.mission.discodeit.annotation.swagger.auth;

import com.sprint.mission.discodeit.dto.AuthUserDto;

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
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = @Content(
                        schema = @Schema(implementation = AuthUserDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 로그인 요청"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "일치하는 사용자 정보를 찾을 수 없음",
                content = @Content(
                        mediaType = "text/plain",
                        schema = @Schema(implementation = String.class),
                        examples = @ExampleObject(
                                value = "아이디 또는 비밀번호가 일치하지 않습니다."
                        )
                )
        )
})
public @interface AuthLoginDocs {
}
