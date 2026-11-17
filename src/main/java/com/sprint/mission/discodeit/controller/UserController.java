package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;
  private final UserStatusService userStatusService;

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> create(
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    UserDto user = userService.create(request, toBinaryContentRequest(profile));
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  private Optional<BinaryContentCreateRequest> toBinaryContentRequest(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(
          new BinaryContentCreateRequest(
              file.getOriginalFilename(), file.getContentType(), file.getBytes()));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @RequestMapping(
      value = "/{userId}",
      method = RequestMethod.PATCH,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    return ResponseEntity.ok(userService.update(userId, request, toBinaryContentRequest(profile)));
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
  public ResponseEntity<UserStatusDto> updateOnline(
      @PathVariable UUID userId, @RequestBody UserStatusUpdateRequest request) {
    return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
  }
}
