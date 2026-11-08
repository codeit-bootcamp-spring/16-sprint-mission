package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public UserDto create(
            @RequestPart("request") UserCreateRequestDto request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        return userService.createUser(request, profile);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public UserDto update(
            @RequestHeader("USER-UUID") UUID userId,
            @RequestBody UserUpdateRequestDto request
    ) {
        return userService.updateUser(userId, request);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(
            @RequestHeader("USER-UUID") UUID userId
    ) {
        userService.deleteUser(userId);
    }

    @RequestMapping(value = "/online", method = RequestMethod.PATCH)
    public UserStatusResponseDto online(
            @RequestHeader("USER-UUID") UUID userId
    ) {
        return userStatusService.updateUserStatusByUserId(userId);
    }
}
