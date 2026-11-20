package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserStatusService userStatusService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> create(
            @RequestPart("user") RegistrationPart request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        BinaryContentCreateRequest profileImage = null;

        if (profile != null && !profile.isEmpty()) {
            profileImage = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );
        }

        UserCreateRequest serviceRequest = new UserCreateRequest(
                request.username(),
                request.email(),
                request.password(),
                profileImage
        );

        UserDto createdUser = userService.create(serviceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId,
            @RequestBody UserUpdateRequest request
    ) {
        UserUpdateRequest serviceRequest = new UserUpdateRequest(
                userId,
                request.username(),
                request.email(),
                request.password(),
                request.profileImage()
        );

        UserDto updatedUser = userService.update(serviceRequest);

        return ResponseEntity.ok(updatedUser);
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> updateWithProfile(
            @PathVariable("userId") UUID userId,
            @RequestPart("user") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        BinaryContentCreateRequest profileImage = null;

        if (profile != null && !profile.isEmpty()) {
            profileImage = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );
        }

        // 수정 대상은 URL의 ID를 사용합니다. 이미지가 null이면 기존 프로필을 유지합니다.
        UserUpdateRequest serviceRequest = new UserUpdateRequest(
                userId,
                request.username(),
                request.email(),
                request.password(),
                profileImage
        );

        return ResponseEntity.ok(userService.update(serviceRequest));
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("userId") UUID userId
    ) {
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {

        List<UserDto> users = userService.findAll();

        return ResponseEntity.ok(users);
    }

    @RequestMapping(
            value = "/{userId}/status",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> updateStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        userStatusService.updateByUserId(userId, request.lastActiveAt());

        UserDto updatedUser = userService.findById(userId);

        return ResponseEntity.ok(updatedUser);
    }

    public record RegistrationPart(String username, String email, String password) {
    }

}