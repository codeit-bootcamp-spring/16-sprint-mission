package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "discodeit.demo",
        name = "seed-users",
        havingValue = "true"
)
public class DemoDataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @Override
    public void run(ApplicationArguments args) {

        createDemoUser("jessie", "jessie@codeit.com");
        createDemoUser("rex", "rex@codeit.com");
        createDemoUser("buzz", "buzz@codeit.com");
        createDemoUser("woody", "woody@codeit.com");

        System.out.println("예시 사용자 데이터 준비 완료");
    }

    private void createDemoUser(
            String username,
            String email
    ) {
        UserResponse user = userService.findAll()
                .stream()
                .filter(existing ->
                        existing.username().equalsIgnoreCase(username)
                )
                .findFirst()
                .orElseGet(() -> userService.create(
                        new CreateUserRequest(
                                username,
                                email,
                                "demo1234"
                        ),
                        null
                ));

        userStatusService.updateByUserId(
                new UpdateUserStatusByUserIdRequest(
                        user.id(),
                        Instant.now()
                )
        );
    }
}