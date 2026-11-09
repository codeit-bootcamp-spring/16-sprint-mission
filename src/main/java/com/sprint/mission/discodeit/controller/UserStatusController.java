package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/v1/userStatus")
public class UserStatusController {
    private final UserStatusService userStatusService;

    @PostMapping
    public String updateUserStatus(@ModelAttribute UpdateUserStatusRequest request,
                                   HttpSession session) {
        userStatusService.updateByUserId((UUID) session.getAttribute("userId"), request);
        return "updateUser";
    }
}
