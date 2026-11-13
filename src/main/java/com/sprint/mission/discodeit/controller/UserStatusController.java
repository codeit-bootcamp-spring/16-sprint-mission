package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    // 사용자의 온라인 상태 업데이트(PATCH)
    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public UserStatus patchUserStatus(@PathVariable UUID userId) {
        return userStatusService.updateByUserId(userId, Instant.now());
    }
}