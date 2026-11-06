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

@RestController // 이 클래스의 메서드 반환값을 JSON 등의 HTTP 응답 본문으로 전달합니다.
@RequestMapping("/api/users") // 이 컨트롤러가 처리할 공통 요청 주소입니다.
@RequiredArgsConstructor // final 필드를 받는 생성자를 Lombok이 만들어줍니다.
public class UserController {

    // 구현체를 직접 new 하지 않고 Spring이 등록한 UserService를 주입받습니다.
    private final UserService userService;

    // 마지막 활동 시각을 갱신하는 서비스입니다.
    // @RequiredArgsConstructor가 생성자를 만들어 Spring이 두 서비스를 주입합니다.
    private final UserStatusService userStatusService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE // JSON 요청을 받습니다.
    )
    public ResponseEntity<UserDto> create(
            @RequestBody UserCreateRequest request // JSON 전체를 DTO로 변환합니다.
    ) {
        UserDto createdUser = userService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    // Postman의 form-data로 사용자 정보와 프로필 이미지 파일을 받습니다.
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> createWithProfile(
            @RequestPart("user") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        BinaryContentCreateRequest profileImage = null;

        // 프로필은 선택 사항입니다. 파일이 있으면 서버에서 바이트로 읽습니다.
        if (profile != null && !profile.isEmpty()) {
            profileImage = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );
        }

        // 사용자 정보와 이미지 데이터를 기존 서비스가 받는 DTO에 합칩니다.
        UserCreateRequest serviceRequest = new UserCreateRequest(
                request.username(),
                request.email(),
                request.password(),
                profileImage
        );

        // 기존 서비스가 중복 검사와 저장을 처리합니다.
        UserDto createdUser = userService.create(serviceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    //update
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId, // 주소에서 수정 대상 ID를 가져옵니다.
            @RequestBody UserUpdateRequest request // JSON에서 수정 정보를 가져옵니다.
    ) {
        // 수정 대상 ID는 주소의 값을 사용합니다.
        UserUpdateRequest serviceRequest = new UserUpdateRequest(
                userId,
                request.username(),
                request.email(),
                request.password(),
                request.profileImage()
        );

        UserDto updatedUser = userService.update(serviceRequest);

        return ResponseEntity.ok(updatedUser); // 200 OK와 JSON을 응답합니다.
    }

    // Postman에서 수정 정보와 새 프로필 이미지 파일을 함께 받습니다.
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

        // 새 파일이 있으면 서비스에 전달할 이미지 DTO로 변환합니다.
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

        // 이미지 교체와 기존 이미지 정리는 이미 구현된 서비스가 처리합니다.
        return ResponseEntity.ok(userService.update(serviceRequest));
    }

    //delete
    // 1. 사용자 삭제
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("userId") UUID userId // 주소에서 삭제할 사용자 ID를 받습니다.
    ) {
        // 기존 서비스에서 사용자와 프로필 이미지, 접속 상태를 삭제합니다.
        userService.delete(userId);

        // 삭제 성공을 뜻하는 204 No Content를 반환합니다.
        // 응답 본문이 없으므로 반환 타입은 ResponseEntity<Void>입니다.
        return ResponseEntity.noContent().build();
    }

    //findall
    // 2. 모든 사용자 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {

        // 서비스가 사용자를 조회하고 비밀번호를 제외한 DTO 목록으로 변환합니다.
        List<UserDto> users = userService.findAll();

        // 200 OK와 사용자 목록을 JSON 배열로 반환합니다.
        // 사용자가 없으면 빈 배열 []을 반환합니다.
        return ResponseEntity.ok(users);
    }

    // statusUpdate
    // 3. 사용자 온라인 상태 업데이트
    @RequestMapping(
            value = "/{userId}/status",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> updateStatus(
            @PathVariable("userId") UUID userId, // 상태를 갱신할 사용자 ID입니다.
            @RequestBody UserStatusUpdateRequest request // 마지막 활동 시각을 받습니다.
    ) {
        // 사용자 ID로 해당 사용자의 상태를 찾아 마지막 활동 시각을 변경합니다.
        userStatusService.updateByUserId(userId, request.lastActiveAt());

        // 변경된 온라인 상태를 포함한 사용자 정보를 다시 조회합니다.
        UserDto updatedUser = userService.findById(userId);

        // 200 OK와 갱신된 사용자 정보를 JSON으로 반환합니다.
        return ResponseEntity.ok(updatedUser);
    }
}