package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "사용자 이름과 비밀번호를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(
            responseCode = "400",
            description = "비밀번호가 일치하지 않거나 요청 JSON이 누락되었거나 잘못됨",
            content = @Content(schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(name = "wrongPassword", value = "Wrong password"))
    )
    @ApiResponse(
            responseCode = "404",
            description = "해당 사용자 이름으로 등록된 사용자를 찾을 수 없음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "userNotFound",
                            value = "User with username tester not found"
                    )
            )
    )
    @PostMapping(value = "login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
