package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserResponse;

import com.sprint.mission.discodeit.entity.BinaryContent;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StaticResourceApiController {

    private final UserService userService;
    private final BinaryContentService binaryContentService;

    @RequestMapping(
            value = "/api/user/findAll",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<UserDto>> findAllUsers() {

        List<UserDto> users = userService.findAll()
                .stream()
                .map(this::toUserDto)
                .toList();

        return ResponseEntity.ok(users);
    }

    @RequestMapping(
            value = "/api/binaryContent/find",
            method = RequestMethod.GET
    )
    public ResponseEntity<BinaryContent> findBinaryContent(
            @RequestParam("binaryContentId")
            UUID binaryContentId
    ) {

        BinaryContent binaryContent =
                binaryContentService.findEntityById(
                        binaryContentId
                );

        return ResponseEntity.ok(binaryContent);
    }

    private UserDto toUserDto(UserResponse user) {
        return new UserDto(
                user.id(),
                user.createdAt(),
                user.updatedAt(),
                user.username(),
                user.email(),
                user.profileId(),
                user.online()
        );
    }
}