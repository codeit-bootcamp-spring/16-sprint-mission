package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;

import java.util.UUID;

public class AuthServiceTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        try {
            AuthService authService = context.getBean(AuthService.class);
            UserService userService = context.getBean(UserService.class);

            String testId = UUID.randomUUID().toString().substring(0, 8);
            String username = "로그인 테스트 " + testId;
            String password = "password123";

            // 테스트 사용자 등록
            UserDto user = userService.create(
                    new UserCreateRequest(
                            username,
                            "login-" + testId + "@example.com",
                            password,
                            null,
                            null,
                            null
                    )
            );

            // 1. 올바른 아이디와 비밀번호로 로그인
            UserDto loggedInUser = authService.login(
                    new LoginRequest(username, password)
            );

            if (loggedInUser.id().equals(user.id())
                    && loggedInUser.username().equals(username)) {
                System.out.println("로그인 성공 테스트: 통과");
            } else {
                throw new AssertionError("로그인 성공 테스트 실패");
            }

            // 2. 잘못된 비밀번호로 로그인
            try {
                authService.login(
                        new LoginRequest(username, "wrong-password")
                );
                throw new AssertionError("잘못된 비밀번호 테스트 실패");
            } catch (IllegalArgumentException e) {
                System.out.println("잘못된 비밀번호 테스트: 통과");
            }

            // 3. 존재하지 않는 사용자로 로그인
            try {
                authService.login(
                        new LoginRequest(
                                "없는사용자-" + testId,
                                password
                        )
                );
                throw new AssertionError("존재하지 않는 사용자 테스트 실패");
            } catch (IllegalArgumentException e) {
                System.out.println("존재하지 않는 사용자 테스트: 통과");
            }

        } finally {
            context.close();
        }
    }
}