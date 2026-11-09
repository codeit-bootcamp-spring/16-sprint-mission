package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/userStatus")
public class UserStatusController {
    private final UserStatusService userStatusService;

    @PostMapping
    public ResponseEntity<UserStatusResponse> updateUserStatus(@ModelAttribute UpdateUserStatusRequest request,
                                                       HttpSession session) {
        UserStatusResponse response = userStatusService.updateByUserId((UUID) session.getAttribute("userId"), request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
