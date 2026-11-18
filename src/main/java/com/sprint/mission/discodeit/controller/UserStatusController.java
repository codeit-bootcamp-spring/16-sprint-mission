package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserStatusApi;
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
public class UserStatusController implements UserStatusApi {
    private final UserStatusService userStatusService;

    @Override
    public ResponseEntity<UserStatus> createUserStatus(UserStatusCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userStatusService.create(request));
    }

    @Override
    public ResponseEntity<UserStatus> getUserStatus(UUID userStatusId) {
        return ResponseEntity.ok(userStatusService.find(userStatusId));
    }

    @Override
    public ResponseEntity<List<UserStatus>> getAllUserStatuses() {
        return ResponseEntity.ok(userStatusService.findAll());
    }

    @Override
    public ResponseEntity<UserStatus> updateUserStatus(UUID userStatusId, UserStatusUpdateRequest request) {
        return ResponseEntity.ok(userStatusService.update(userStatusId, request));
    }

    @Override
    public ResponseEntity<UserStatus> updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request) {
        return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
    }

    @Override
    public ResponseEntity<Void> deleteUserStatus(UUID userStatusId) {
        userStatusService.delete(userStatusId);
        return ResponseEntity.noContent().build();
    }
}