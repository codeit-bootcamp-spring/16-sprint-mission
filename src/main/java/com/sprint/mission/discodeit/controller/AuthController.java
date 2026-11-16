package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ApiSpecDto;
import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

import com.sprint.mission.discodeit.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST
    )
    public ResponseEntity<ApiSpecDto.UserView> login(
            @RequestBody LoginRequest request
    ) {
        UserResponse user = authService.login(request);

        ApiSpecDto.UserView response =
                new ApiSpecDto.UserView(
                        user.id(),
                        user.createdAt(),
                        user.updatedAt(),
                        user.username(),
                        user.email(),
                        user.profileId()
                );

        return ResponseEntity.ok(response);
    }
}