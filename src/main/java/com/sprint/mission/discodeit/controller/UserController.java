package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> postUser(@RequestPart("username") String username,
                                   @RequestPart("email") String email,
                                   @RequestPart("password") String password,
                                   @RequestPart(required = false) MultipartFile profile) throws IOException {

        BinaryContentRequest contentRequest = null;
        if (profile != null) {
            contentRequest = new BinaryContentRequest(profile.getOriginalFilename(), profile.getSize(),
                    profile.getContentType(), profile.getBytes());
        }
        CreateUserRequest request = new CreateUserRequest(username, email, password, contentRequest);

        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> response = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/update")
    public ResponseEntity<UserResponse> postUpdateUser(@ModelAttribute UpdateUserRequest request,
                                                       HttpSession session) {
        UserResponse response = userService.update((UUID) session.getAttribute("userId"), request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteUser(HttpSession session) {
        userService.delete((UUID) session.getAttribute("userId"));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
