package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.service.basic.UserStatusService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Hidden
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/userStatus")
public class UserStatusController {
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserStatusResponse> findUserStatusByUserId(@NotNull @PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userStatusService.findByUserId(userId));
    }
}
