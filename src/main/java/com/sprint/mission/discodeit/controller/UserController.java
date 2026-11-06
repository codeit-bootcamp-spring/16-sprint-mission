package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public String postUser(@RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam(required = false) MultipartFile profile,
                           Model model) throws IOException {

        BinaryContentRequest contentRequest = null;
        if (profile != null) {
            contentRequest = new BinaryContentRequest(profile.getOriginalFilename(), profile.getSize(),
                    profile.getContentType(), profile.getBytes());
        }
        CreateUserRequest request = new CreateUserRequest(username, email, password, contentRequest);

        UserResponse response = userService.create(request);
        model.addAttribute("username", response.username());
        model.addAttribute("email", response.email());
        return "signUpResult";
    }

    @PostMapping("/update")
    public String postUpdateUser(@ModelAttribute UpdateUserRequest request,
                                 HttpSession session) {
        userService.update((UUID) session.getAttribute("userId"), request);
        return "updateResult";
    }

    @GetMapping("/update")
    public String showUpdateForm() {
        return "updateForm";
    }

    @DeleteMapping("/delete")
    public String deleteUser(HttpSession session) {
        userService.delete((UUID) session.getAttribute("userId"));
        return "delete";
    }
}
