package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 유저 생성 (JSON)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createJson(@RequestBody UserCreateRequest userCreateRequest) {
        User createdUser = userService.create(userCreateRequest, Optional.empty());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(createdUser));
    }

    // 유저 생성 (JSON + 프로필 이미지 파일)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = toBinaryContentRequest(profile);
        User createdUser = userService.create(userCreateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(createdUser));
    }

    // 유저 전체 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    // 유저 한 명 조회
    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> find(@PathVariable("userId") UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.find(userId));
    }

    // 유저 수정 (JSON)
    @RequestMapping(path = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateJson(
            @PathVariable("userId") UUID userId,
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        User updatedUser = userService.update(userId, userUpdateRequest, Optional.empty());
        return ResponseEntity.status(HttpStatus.OK).body(toDto(updatedUser));
    }

    // 유저 수정 (JSON + 프로필 이미지 파일)
    @RequestMapping(path = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = toBinaryContentRequest(profile);
        User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.OK).body(toDto(updatedUser));
    }

    // 유저 삭제
    @RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 업로드된 파일을 서비스가 받는 형태로 바꿔주는 도우미 메서드
    private Optional<BinaryContentCreateRequest> toBinaryContentRequest(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }
        try {
            BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
            return Optional.of(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // User 엔티티를 비밀번호가 빠진 UserDto로 바꿔주는 도우미 메서드
    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                null
        );
    }
}