package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthUserDto;
import com.sprint.mission.discodeit.dto.request.authlogin.AuthLoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 사용자 로그인(POST)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AuthUserDto postLogin(@RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }
}