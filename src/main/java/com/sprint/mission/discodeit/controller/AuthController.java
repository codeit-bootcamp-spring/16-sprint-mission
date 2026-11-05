package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.AuthUserRequest;
import com.sprint.mission.discodeit.dto.auth.AuthUserResponse;
import com.sprint.mission.discodeit.service.basic.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(@RequestBody AuthUserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.authService.login(userRequest));
    }
}
