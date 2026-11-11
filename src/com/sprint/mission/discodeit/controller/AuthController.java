package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDto;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST
    )
    public ResponseEntity<UserDto> login(
            @RequestBody LoginRequest request
    ) {
        UserDto userDto = authService.login(request);

        return ResponseEntity.ok(userDto);
    }
}