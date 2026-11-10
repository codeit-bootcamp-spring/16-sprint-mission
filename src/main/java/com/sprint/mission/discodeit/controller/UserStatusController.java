package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/user-statuses")
@RequiredArgsConstructor
public class UserStatusController {
    private final UserStatusService userStatusService;

    @PostMapping
    public ResponseEntity<UserStatus> createUserStatus(@RequestBody UserStatusCreateRequest request) {
        UserStatus userStatus = userStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userStatus);
    }

    @GetMapping("/{userStatusId}")
    public ResponseEntity<UserStatus> getUserStatus(@PathVariable UUID userStatusId) {
        UserStatus userStatus = userStatusService.find(userStatusId);
        return ResponseEntity.ok(userStatus);
    }

    @GetMapping
    public ResponseEntity<List<UserStatus>> getAllUserStatuses() {
        List<UserStatus> statuses = userStatusService.findAll();
        return ResponseEntity.ok(statuses);
    }

    @PutMapping("/{userStatusId}")
    public ResponseEntity<UserStatus> updateUserStatus(
            @PathVariable UUID userStatusId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatus updatedStatus = userStatusService.update(userStatusId, request);
        return ResponseEntity.ok(updatedStatus);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatus updatedStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.ok(updatedStatus);
    }

    @DeleteMapping("/{userStatusId}")
    public ResponseEntity<Void> deleteUserStatus(@PathVariable UUID userStatusId) {
        userStatusService.delete(userStatusId);
        return ResponseEntity.noContent().build();
    }
}
