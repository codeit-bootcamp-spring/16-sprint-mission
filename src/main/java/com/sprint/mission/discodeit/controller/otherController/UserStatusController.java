package com.sprint.mission.discodeit.controller.otherController;

import com.sprint.mission.discodeit.entity.newDomain.UserStatus;
import com.sprint.mission.discodeit.service.other.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    // 사용자의 온라인 상태 업데이트(PATCH)
    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public UserStatus patchUserStatus(@PathVariable UUID userId) {
        return userStatusService.updateByUserId(userId, Instant.now());
    }
}
