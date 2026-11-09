package com.sprint.mission.discodeit.controll;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(
            @RequestBody UserCreateDto dto
    ) {
        UserDto user = userService.createUser(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }
}
