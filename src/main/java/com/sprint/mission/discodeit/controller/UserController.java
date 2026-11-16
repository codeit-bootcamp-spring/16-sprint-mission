package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(summary = "User 등록")
  @ApiResponse(
          responseCode = "201",
          description = "User가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = User.class))
  )
  @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함"
  )
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<User> create(
          @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
          @Parameter(description = "User 프로필 이미지")
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);
    User createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUser);
  }

  @Operation(summary = "User 정보 수정")
  @ApiResponse(
          responseCode = "200",
          description = "User 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = User.class))
  )
  @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
  @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함"
  )
  @PatchMapping(
          path = "/{userId}",
          consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<User> update(
          @Parameter(description = "수정할 User ID") @PathVariable("userId") UUID userId,
          @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
          @Parameter(description = "수정할 User 프로필 이미지")
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUser);
  }

  @Operation(summary = "User 삭제")
  @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨")
  @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
  @DeleteMapping(path = "/{userId}")
  public ResponseEntity<Void> delete(
          @Parameter(description = "삭제할 User ID") @PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
  }

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(
          responseCode = "200",
          description = "User 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
  )
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(users);
  }

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponse(
          responseCode = "200",
          description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(schema = @Schema(implementation = UserStatus.class))
  )
  @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음")
  @PatchMapping(path = "/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
          @Parameter(description = "상태를 변경할 User ID") @PathVariable("userId") UUID userId,
          @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUserStatus);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                profileFile.getOriginalFilename(),
                profileFile.getContentType(),
                profileFile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}