package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.dto.authentication.LoginResponse;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag (
   name = "Auth",
   description = "사용자 로그인 및 인증 API"
)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "User 로그인",
            description = "사용자명과 비밀번호를 검증하고, 인증에 성공하면 로그인한 User의 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인에 성공하여 User 정보를 반환합니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = LoginResponse.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "id": "550e8400-e29b-41d4-a716-446655440000",
                                  "createdAt": "2026-11-17T06:00:00Z",
                                  "updatedAt": "2026-11-17T06:00:00Z",
                                  "username": "student01",
                                  "email": "student01@example.com",
                                  "profileId": null
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "비밀번호 불일치, 유효성 검증 실패 또는 잘못된 요청 형식",
                    content = {
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string"),
                                    examples = {
                                            @ExampleObject(
                                                    name = "비밀번호 불일치",
                                                    value = "입력하신 비밀번호 Example123!가 옳바르지 않습니다."
                                            ),
                                            @ExampleObject(
                                                    name = "유효성 검증 실패",
                                                    value = "요청값이 올바르지 않습니다."
                                            )
                                    }
                            ),
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "입력한 사용자명에 해당하는 User가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(
                                    value = "사용자 이름이 student01인 사용자를 찾을 수 없습니다."
                            )
                    )
            )
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse loginResponse = this.authService.login(request);

        return ResponseEntity.ok(loginResponse);
    }
}
