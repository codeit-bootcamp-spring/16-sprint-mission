package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.DiscodeitApplication;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
            BinaryContentRepository binaryContentRepository =
                    context.getBean(BinaryContentRepository.class);

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

            // 프로필 이미지 생성 및 교체 테스트
            testProfileImageReplacement(
                    userService,
                    binaryContentRepository,
                    testId
            );

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

    /**
     * 프로필 이미지 생성 → 교체 → 이전 이미지 삭제 여부 테스트
     */
    private static void testProfileImageReplacement(
            UserService userService,
            BinaryContentRepository binaryContentRepository,
            String testId
    ) {
        byte[] firstImageData = "first-image".getBytes();
        byte[] secondImageData = "second-image".getBytes();

        // 첫 번째 프로필 이미지가 포함된 사용자 생성
        UserDto user = userService.create(
                new UserCreateRequest(
                        "이미지 테스트 사용자 " + testId,
                        "image-test-" + testId + "@example.com",
                        "password123",
                        "first.png",
                        "image/png",
                        (long) firstImageData.length,
                        firstImageData
                )
        );

        UUID firstImageId = user.profileImageId();

        if (firstImageId == null
                || binaryContentRepository.findById(firstImageId).isEmpty()) {
            throw new AssertionError("첫 번째 프로필 이미지 생성 실패");
        }

        System.out.println("첫 번째 프로필 이미지 생성: 통과");

        // 두 번째 이미지로 교체
        userService.update(
                new UserUpdateRequest(
                        user.id(),
                        user.username(),
                        user.email(),
                        "password123",
                        "second.png",
                        "image/png",
                        (long) secondImageData.length,
                        secondImageData
                )
        );

        // 수정된 사용자 조회
        UserDto updatedUser = userService.findById(user.id())
                .orElseThrow(() -> new AssertionError("이미지 교체 후 사용자 조회 실패"));

        UUID secondImageId = updatedUser.profileImageId();

        // 프로필 이미지 ID 변경 확인
        if (secondImageId == null || secondImageId.equals(firstImageId)) {
            throw new AssertionError("프로필 이미지 ID가 변경되지 않았습니다.");
        }

        // 이전 이미지 삭제 여부 확인
        if (binaryContentRepository.findById(firstImageId).isPresent()) {
            throw new AssertionError("이전 프로필 이미지가 삭제되지 않았습니다.");
        }

        System.out.println("이전 프로필 이미지 삭제: 통과");

        // 새 이미지 저장 여부 확인
        if (binaryContentRepository.findById(secondImageId).isEmpty()) {
            throw new AssertionError("새 프로필 이미지가 저장되지 않았습니다.");
        }

        System.out.println("새 프로필 이미지 저장: 통과");

        // 테스트 사용자 삭제
        userService.deleteById(user.id());

        if (userService.findById(user.id()).isEmpty()) {
            System.out.println("이미지 테스트 사용자 정리: 통과");
        } else {
            throw new AssertionError("이미지 테스트 사용자 삭제 실패");
        }
    }
}