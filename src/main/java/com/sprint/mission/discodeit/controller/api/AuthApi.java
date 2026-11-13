package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {
  @Operation(summary = "로그인")
  @ApiResponse(responseCode = "200", description = "로그인 성공")
  @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
  @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음")
  ResponseEntity<UserDto> login(UserLoginRequest request);
}
