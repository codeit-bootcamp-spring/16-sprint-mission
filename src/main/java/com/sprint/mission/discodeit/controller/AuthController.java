package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.AuthUserDto;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "로그인",
            description = "사용자 이름과 비밀번호로 로그인합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않거나 요청 형식이 올바르지 않음")
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    public ResponseEntity<AuthUserDto> login(
            @RequestBody AuthLoginRequest request
    ) {
        AuthUserDto user = authService.login(request);

        return ResponseEntity.ok(user);
    }

}
