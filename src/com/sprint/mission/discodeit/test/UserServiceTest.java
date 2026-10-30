package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.DiscodeitApplication;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;

import java.util.UUID;

public class UserServiceTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        try {
            UserService userService = context.getBean(UserService.class);

            String testId = UUID.randomUUID().toString().substring(0, 8);

            // 사용자 등록
            UserDto user = userService.create(
                    new UserCreateRequest(
                            "테스트 사용자 " + testId,
                            "test-" + testId + "@example.com",
                            "password123",
                            null,
                            null,
                            null
                    )
            );

            if (userService.findById(user.id()).isPresent()) {
                System.out.println("사용자 등록 테스트: 통과");
            } else {
                throw new AssertionError("사용자 등록 테스트 실패");
            }

            // 사용자 조회
            UserDto foundUser = userService.findById(user.id())
                    .orElseThrow(() -> new AssertionError("사용자 조회 실패"));

            if (foundUser.username().equals("테스트 사용자 " + testId)) {
                System.out.println("사용자 조회 테스트: 통과");
            } else {
                throw new AssertionError("사용자 이름이 예상과 다릅니다.");
            }

            // 사용자 수정
            String updatedEmail = "updated-" + testId + "@example.com";

            userService.update(
                    new UserUpdateRequest(
                            user.id(),
                            "수정된 사용자 " + testId,
                            updatedEmail,
                            "password123",
                            null,
                            null,
                            null
                    )
            );

            UserDto updatedUser = userService.findById(user.id())
                    .orElseThrow(() ->
                            new AssertionError("수정된 사용자 조회 실패"));

            if (updatedUser.username().equals("수정된 사용자 " + testId)
                    && updatedUser.email().equals(updatedEmail)) {
                System.out.println("사용자 수정 테스트: 통과");
            } else {
                throw new AssertionError("사용자 수정 테스트 실패");
            }

            // 사용자 삭제
            userService.deleteById(user.id());

            if (userService.findById(user.id()).isEmpty()) {
                System.out.println("사용자 삭제 테스트: 통과");
            } else {
                throw new AssertionError("사용자 삭제 테스트 실패");
            }

        } finally {
            context.close();
        }
    }
}