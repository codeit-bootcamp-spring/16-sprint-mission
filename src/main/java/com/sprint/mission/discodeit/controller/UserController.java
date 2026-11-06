package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public UserResponseDto create(
            @RequestBody UserCreateRequestDto request
    ) {
        return userService.createUser(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserResponseDto> findAccessible() {
        return userService.findAllUsers();
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public UserResponseDto update(
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
