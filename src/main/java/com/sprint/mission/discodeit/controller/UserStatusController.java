package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/userStatus")
public class UserStatusController {
    UserStatusService userStatusService;

    @PostMapping
    public String updateUserStatus() {
        // userStatusService.update();
        return "updateUser";
    }
}
