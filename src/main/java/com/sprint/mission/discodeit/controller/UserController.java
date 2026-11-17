
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(
            @RequestPart("request") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();

        if (profile != null && !profile.isEmpty()) {
            profileRequest = Optional.of(
                    new BinaryContentCreateRequest(
                            profile.getOriginalFilename(),
                            profile.getContentType(),
                            profile.getBytes()
                    )
            );
        }

        User user = userService.create(request, profileRequest);
        return userService.find(user.getId());
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserDto find(@PathVariable UUID userId) {
        return userService.find(userId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public UserDto update(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest request
    ) {
        userService.update(userId, request, Optional.empty());
        return userService.find(userId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public UserStatus updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        return userStatusService.updateByUserId(userId, request);
    }
}
