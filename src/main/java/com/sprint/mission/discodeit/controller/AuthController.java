package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.AuthUserRequest;
import com.sprint.mission.discodeit.dto.auth.AuthUserResponse;
import com.sprint.mission.discodeit.service.basic.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<AuthUserResponse> login(@Valid @RequestBody AuthUserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.authService.login(userRequest));
    }
}
