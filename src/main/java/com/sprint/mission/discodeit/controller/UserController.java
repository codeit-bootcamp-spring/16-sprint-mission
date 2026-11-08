package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.UserStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> create(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.create(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping("/update/{userId}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID userId , @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.update(userId, userRequestDto));
    }

    @DeleteMapping("/delete/{userId}")
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    @PostMapping("/online/{userId}")
    public ResponseEntity<Map<UUID, UserStatusResponseDto>> userOnline(@PathVariable UUID userId, @RequestBody UserStatusUpdateDto userStatusUpdateDto) {
        UserStatusResponseDto userStatusResponseDto = userStatusService.updateByUserId(userId);
        return ResponseEntity.ok(Map.of(userId, userStatusResponseDto));
    }

}
