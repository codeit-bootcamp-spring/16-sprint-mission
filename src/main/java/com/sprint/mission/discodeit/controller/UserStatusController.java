package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusFindResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateResponse;
import com.sprint.mission.discodeit.service.basic.UserStatusService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users/status")
public class UserStatusController {
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserStatusFindResponse> findUserStatusByUserId(@NotNull @PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userStatusService.findByUserId(userId));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusUpdateResponse> updateUserStatus(@NotNull @PathVariable UUID userId,
                                                                     @Valid @RequestBody UserStatusUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userStatusService.updateByUserId(userId, updateRequest));
    }
}
