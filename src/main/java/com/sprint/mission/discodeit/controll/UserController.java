package com.sprint.mission.discodeit.controll;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Controller
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
            @RequestPart("user") UserCreateDto dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UserDto user = userService.createUser(dto, profile);
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

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> update(
            @PathVariable("id") UUID id,
            @RequestPart("user") UserUpdateDto dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UserUpdateDto updateDto = new UserUpdateDto(
                id,
                dto.username(),
                dto.age(),
                dto.password()
        );

        return ResponseEntity.ok(userService.updateUser(updateDto, profile));
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