package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequestDto request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) throws IOException {
        return userService.createUser(request, profile);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PatchMapping("/{userId}")
    public UserResponseDto update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequestDto request
    ) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID userId
    ) {
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}/userStatus")
    public UserStatusResponseDto online(
            @PathVariable UUID userId
    ) {
        return userStatusService.updateUserStatusByUserId(userId);
    }
}
