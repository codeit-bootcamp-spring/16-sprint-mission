package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;
    private final SwaggerIndexTransformer indexPageTransformer;


  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(summary = "User 등록")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
          @ApiResponse(responseCode = "400",
                  description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                  content = @Content(mediaType = "*/*",
                          examples = @ExampleObject(value = "User with email {email} already exists")))
  })
  public ResponseEntity<UserDto> create(
          @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
          @Parameter(description = "User 프로필 이미지")
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto userDto = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userDto);
  }

  @GetMapping("/{userId}")
  @Operation(summary = "사용자 조회", description = "ID로 사용자 조회")
  @ApiResponse(responseCode = "200", description = "성공적으로 {userId}의 유저를 찾음")
  public ResponseEntity<UserDto> find(
          @PathVariable("userId")
          @Parameter(description = "찾을 User ID", required = true)
          UUID userId) {
    UserDto userDto = userService.find(userId);
    return ResponseEntity.ok(userDto);
  }


  @PatchMapping(value = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(summary = "User 정보 수정")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
          @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                  content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "user with email {newEmail} already exists"))),
          @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                  content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "User with id {userId} not found")))
  })
  public ResponseEntity<UserDto> update(
      @PathVariable("userId")
      @Parameter(description = "수정할 User ID", required = true)
      UUID userId,
      @RequestPart(value = "userUpdateRequest", required = true) UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false)
      @Parameter(description = "수정할 User 프로필 이미지")
      MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto userDto = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  @DeleteMapping("/{userId}")
  @Operation(summary = "User 삭제")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "User with id {id} not found")))
  })
  public ResponseEntity<Void> delete(
          @PathVariable("userId")
          @Parameter(description = "삭제할 User ID", required = true)
          UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @PatchMapping("/{userId}/userStatus")
  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트 됨"),
          @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
                  content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "UserStatus with userId {userId} not found")))
  })
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @PathVariable("userId")
      @Parameter(description = "상태를 변경할 User ID", required = true)
      UUID userId,
      @RequestBody(required = true) UserStatusUpdateRequest request) {
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
