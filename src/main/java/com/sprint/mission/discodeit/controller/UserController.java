package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserListResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
// 클라이언트의 요청을 특정 메서드로 매핑하는 Spring 애너테이션입니다.
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserMapper userMapper;

    // Controller 계층은 일반적으로 Controller 인터페이스를 구현하지 않습니다.
    // Controller 계층은 다른 컨트롤러로 교체할 경우가 적기 때문입니다.
    // POST HTTP 메서드를 처리하는 스프링 애너테이션입니다.
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        // this.userService.create(request) 를 호출하여 발생한 예외가 있다면
        // 전역 예외 처리기인 GlobalExpceptionHandler가 이를 자동으로 감지하여
        // 해당하는 예외에 부합하는 메서드를 호출 시킵니다.
        // 따라서, 이 메서드를 포함한 컨트롤러의 메서드들에는
        // 예외 처리를 위한 별도 로직이나 애너테이션이 필요하지않습니다.
        User user = this.userService.create(request);

        // HTTP 200 상태를 반환합니다.
        // User객체를 HTTP 응답 body에 매핑합니다(JSON 직렬화).
        return ResponseEntity.ok(user);
    };

    @PatchMapping("/{user-id}")
    // @PathVariable은 특정 리소스로 들어온 값을 가져옵니다.
    // @RequestBody는 요청 본문의 값을 가져와서 DTO에 매핑합니다.
    public ResponseEntity<User> updateUser(@PathVariable("user-id") UUID userId, @RequestBody UserUpdateRequest request) {
        User user = this.userService.update(userId, request);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        this.userService.delete(userId);

        // UserService의 delete()의 반환값이 없더라고, 클라이언트에게 이를 알려주는
        // HTTP 응답(204 No Content)을 보내야합니다.
        // HTTP 상태 코드인 204 No Content를 가진 응답을 생성해서 반환합니다.
        // ResponseEntity.noContent() 는 204 HTTP 상태 코드를 설정하는
        // 빌더 객체를 생성합니다.
        // .build()로 ResponseEntity 객체를 생성하여 반환합니다.
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    // getAllUser()의 호출로 인해 발생하는 예외는 없습니다.
    public ResponseEntity<UserListResponse> getAllUsers() {
        List<UserResponse> userList = this.userService.findAll();
        UserListResponse userListResponse = new UserListResponse(userList);

        // ResponseEntity<UserListResponse> 타입의 객체를 생성하여 반환합니다.
        return ResponseEntity.ok(userListResponse);
    }

    // @PatchMapping: 기존 사용자의 상태를 변경할 때
    // @PostMapping: 새로운 상태 변경 이벤트를 생성하거나 특정 동작을 실행하도록 요청할 때
    @PatchMapping("/{user-id}")
    public ResponseEntity<UserResponse> updateUserOnlineStatus(
            @PathVariable("user-id") UUID userId, @RequestBody UserUpdateRequest request) {
        // UserResponse DTO에 온라인 여부를 알려주는 필드를 추가해야한다. // 알고 보니 이미 있었다.
        // this.userStatusService.xxx
        User user = this.userService.update(userId, request); // <- 이게 userResponse를 반환하면 다른 과정 x
        UserStatus userStatus = this.userService.findUserStatus(userId);

        UserResponse userResponse = this.userMapper.toResponse(user, userStatus); // toResponse(user, userStatus)
        return ResponseEntity.ok(userResponse);
    }
}
