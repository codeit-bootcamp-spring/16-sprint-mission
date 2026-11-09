package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 사용자 로그인
    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST
    )
    public User login(@RequestBody LoginRequest request) {

        // 기존 AuthService의 로그인 기능 호출
        return authService.login(request);
    }
}
