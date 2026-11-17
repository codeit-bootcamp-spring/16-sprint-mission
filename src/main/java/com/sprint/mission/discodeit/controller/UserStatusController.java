package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    // 사용자 온라인 상태 업데이트
    @RequestMapping(path = "/{userId}/userStatus", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateByUserId(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
    }
}