package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.dto.authentication.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthMapper authMapper;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = this.authService.login(request);
        LoginResponse loginResponse = this.authMapper.toLoginResponse(user);

        return ResponseEntity.ok(loginResponse);
    }
}
