package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }


    @RequestMapping(method = RequestMethod.GET, value = {"/findAll"})
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestPart("request") UserCreateRequest request, @RequestPart(value = "profile", required = false)
            MultipartFile profile) {
        User user = userService.create(request, profile);
        return userService.findById(user.getId());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId){
        userService.delete(userId);
    }

    @RequestMapping(method = RequestMethod.PATCH,value = "/{userId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto update(@PathVariable UUID userId,@RequestPart("request") UserUpdateRequest request,
                          @RequestPart(value = "profile", required = false)MultipartFile profile) {
        userService.update(new UserUpdateRequest(userId, request.name(), request.password(), request.email()), profile);
        return userService.findById(userId);
    }



    @RequestMapping(method = RequestMethod.PATCH,value = "/{userId}/online-status")
    public UserDto onlineStatus(@PathVariable UUID userId){
        userStatusService.updateByUserId(userId,Instant.now());
        return userService.findById(userId);
    }
}

