package com.sprint.mission.discodeit.controll;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto> create(@RequestBody UserCreateDto dto) {
        UserDto user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserDto> update(
            @PathVariable("id") UUID id,
            @RequestBody UserUpdateDto dto
    ) {
        UserUpdateDto updateDto = new UserUpdateDto(
                id,
                dto.username(),
                dto.age(),
                dto.password(),
                dto.profile()
        );

        return ResponseEntity.ok(userService.updateUser(updateDto));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}/status", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateStatus(@PathVariable("id") UUID id) {
        userStatusService.updateByUserId(id, Instant.now());
        return ResponseEntity.noContent().build();
    }
}