package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusFindResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateResponse;
import com.sprint.mission.discodeit.service.basic.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/users/status")
public class UserStatusController {
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserStatusFindResponse> findUserStatusByUserId(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userStatusService.findByUserId(userId));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusUpdateResponse> updateUserStatus(@PathVariable UUID userId,
                                                                     @RequestBody UserStatusUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userStatusService.updateByUserId(userId, updateRequest));
    }
}
