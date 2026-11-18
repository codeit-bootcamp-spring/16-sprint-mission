package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 및 로그인 API")
public interface AuthApi {

    @Operation(summary = "로그인", description = "사용자 이름과 비밀번호로 로그인을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치 또는 잘못된 요청 형식"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 이름")
    })
    @PostMapping("/login")
    ResponseEntity<User> login(@RequestBody LoginRequest loginRequest);
}