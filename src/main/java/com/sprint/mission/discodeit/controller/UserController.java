package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @Override
    public ResponseEntity<User> createUser(UserCreateRequest request, MultipartFile profile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request, profile));
    }

    @Override
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    public ResponseEntity<User> updateUser(UUID userId, UserUpdateRequest request, MultipartFile profile) {
        return ResponseEntity.ok(userService.update(userId, request, profile));
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserStatus> updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request) {
        return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
    }
}