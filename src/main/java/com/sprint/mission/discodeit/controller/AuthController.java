package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/v1/users")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        HttpSession session) {
        UserResponse response = authService.login(request);
        session.setAttribute("userId", response.userId());
        return "login";
    }
}
