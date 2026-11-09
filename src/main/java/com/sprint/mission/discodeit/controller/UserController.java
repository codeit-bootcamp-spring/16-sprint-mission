package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserListResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = this.userService.create(request);
        UserStatus userStatus = this.userService.findUserStatus(user.getId());
        UserResponse userResponse = this.userMapper.toResponse(user, userStatus);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    };

    @PatchMapping("/{user-id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("user-id") UUID userId, @Valid @RequestBody UserUpdateRequest request) {
        User user = this.userService.update(userId, request);
        UserStatus userStatus = this.userService.findUserStatus(user.getId());
        UserResponse userResponse = this.userMapper.toResponse(user, userStatus);

        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        this.userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers() {
        List<UserResponse> userList = this.userService.findAll();
        UserListResponse userListResponse = new UserListResponse(userList);

        return ResponseEntity.ok(userListResponse);
    }

    @PatchMapping("/{user-id}/status")
    public ResponseEntity<UserStatusResponse> updateUserOnlineStatus(
            @PathVariable("user-id") UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        UserStatusResponse userStatusResponse = this.userStatusService.updateByUserId(userId);

        return ResponseEntity.ok(userStatusResponse);
    }
}
