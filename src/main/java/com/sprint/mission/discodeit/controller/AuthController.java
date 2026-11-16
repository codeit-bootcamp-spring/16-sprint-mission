package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.AuthUserRequest;
import com.sprint.mission.discodeit.dto.auth.AuthUserResponse;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.basic.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "사용자 로그인", description = "사용자가 이름과 비밀번호를 통해 로그인함")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "아이디 또는 비밀번호가 일치하지 않음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자의 접속 상태이 존재하지 않음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<AuthUserResponse> login(@Valid @RequestBody AuthUserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.authService.login(userRequest));
    }
}
