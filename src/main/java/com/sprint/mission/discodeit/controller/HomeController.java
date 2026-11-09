package com.sprint.mission.discodeit.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {
    // "/"는 static/index.html(사용자 목록)이 서빙한다
    @GetMapping("/login")
    public String home() {
        return "home";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "signUp";
    }
}
