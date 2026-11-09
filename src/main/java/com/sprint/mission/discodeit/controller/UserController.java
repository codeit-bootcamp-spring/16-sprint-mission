package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserFindResponse>> findAllUser() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.findAll());
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserCreateResponse> createUser(@Valid @RequestPart("user") UserCreateRequest createRequest,
                                                         @RequestPart(name = "profileImage", required = false) MultipartFile profileImage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.create(createRequest, profileImage));
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserUpdateResponse> updateUser(@NotNull @PathVariable UUID userId,
                                                         @Valid @RequestPart("user") UserUpdateRequest updateRequest,
                                                         @RequestPart(name = "profileImage", required = false) MultipartFile profileImage) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.update(userId, updateRequest, profileImage));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@NotNull @PathVariable UUID userId) {
        this.userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
