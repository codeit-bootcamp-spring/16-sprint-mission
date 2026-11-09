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
import org.springframework.web.bind.annotation.*;

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


    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserDto create(@RequestBody UserCreateRequest request) {
        User user =  userService.create(request,null);
        return userService.findById(user.getId());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId){
        userService.delete(userId);
    }

    @RequestMapping(method = RequestMethod.PATCH,value = "/{userId}")
    public UserDto update(@PathVariable UUID userId,@RequestBody UserUpdateRequest request) {
        UserUpdateRequest updateRequest = new UserUpdateRequest(userId,request.name(),request.password(),request.email());
        userService.update(updateRequest,null);
        return userService.findById(userId);
    }

    @RequestMapping(method = RequestMethod.PATCH,value = "/{userId}/online-status")
    public UserDto onlineStatus(@PathVariable UUID userId){
        userStatusService.updateByUserId(userId,Instant.now());
        return userService.findById(userId);
    }
}

