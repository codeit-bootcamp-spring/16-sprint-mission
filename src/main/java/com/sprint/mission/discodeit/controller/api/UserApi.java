package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {
  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "User 등록")
  @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨")
  @ApiResponse(responseCode = "409", description = "같은 email 또는 username를 사용하는 User가 이미 존재함")
  ResponseEntity<UserDto> create(
      UserCreateRequest userCreateRequest,
      @Parameter(description = "User 프로필 이미지") MultipartFile profile);

  @Operation(summary = "User 삭제")
  @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨")
  @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
  ResponseEntity<Void> delete(@Parameter(description = "삭제할 User ID") UUID userId);

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨")
  @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음")
  ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @Parameter(description = "상태를 변경할 User ID") UUID userId, UserStatusUpdateRequest request);

  @Operation(summary = "User 정보 수정")
  @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨")
  @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
  @ApiResponse(responseCode = "409", description = "같은 email 또는 username를 사용하는 User가 이미 존재함")
  ResponseEntity<UserDto> update(
      @Parameter(description = "수정할 User ID") UUID userId,
      UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 이미지") MultipartFile profile);
}
