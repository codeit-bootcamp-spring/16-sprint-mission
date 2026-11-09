package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateOnlineStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserStatusResponse;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponse> create(
            @RequestBody CreateUserRequest request
    ) {
        UserResponse response = userService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.GET
    )
    public UserResponse findById(
            @PathVariable("userId") UUID userId
    ) {
        return userService.findById(userId);
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH
    )
    public UserResponse update(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateUserRequest request
    ) {
        if (request.id() != null
                && !userId.equals(request.id())) {
            throw new IllegalArgumentException(
                    "URL의 사용자 ID와 요청 본문의 ID가 다릅니다."
            );
        }

        UpdateUserRequest updateRequest = new UpdateUserRequest(
                userId,
                request.username(),
                request.email(),
                request.password(),
                request.profile()
        );

        return userService.update(updateRequest);
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("userId") UUID userId
    ) {
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            value = "/{userId}/status",
            method = RequestMethod.PATCH
    )
    public UserStatusResponse updateOnlineStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody(required = false)
            UpdateOnlineStatusRequest request
    ) {
        Instant lastSeenAt;

        if (request == null || request.lastSeenAt() == null) {
            lastSeenAt = Instant.now();
        } else {
            lastSeenAt = request.lastSeenAt();
        }

        UpdateUserStatusByUserIdRequest updateRequest =
                new UpdateUserStatusByUserIdRequest(
                        userId,
                        lastSeenAt
                );

        return userStatusService.updateByUserId(updateRequest);
    }
}