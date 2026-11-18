package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.exception.CustomErrorResponse;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/login")
@Tag(name="사용자인증",description = "사용자 로그인 API")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "로그인",description = "가입한 사용자 이름과 비밀번호로 로그인 시도 하고 성공하면 사용자정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치 또는 잘못된 요청 JSON",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"400\",\"message\":\"비밀번호가 일치하지 않습니다\"}"))),
            @ApiResponse(responseCode = "404", description = "가입한 사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"404\",\"message\":\"유저를 찾을 수 없음\"}")))
    })
    @RequestMapping(method = RequestMethod.POST)
    public UserResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}
