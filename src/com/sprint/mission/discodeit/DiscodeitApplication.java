package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context =
                     SpringApplication.run(DiscodeitApplication.class, args)) {

            // Spring Container에서 Service Bean 가져오기
            UserService userService = context.getBean(UserService.class);
            AuthService authService = context.getBean(AuthService.class);
            ChannelService channelService = context.getBean(ChannelService.class);
            MessageService messageService = context.getBean(MessageService.class);
            ReadStatusService readStatusService =
                    context.getBean(ReadStatusService.class);
            UserStatusService userStatusService =
                    context.getBean(UserStatusService.class);
            BinaryContentService binaryContentService =
                    context.getBean(BinaryContentService.class);

            // 등록된 Bean 확인
            System.out.println("UserService Bean: "
                    + userService.getClass().getSimpleName());
            System.out.println("AuthService Bean: "
                    + authService.getClass().getSimpleName());
            System.out.println("ChannelService Bean: "
                    + channelService.getClass().getSimpleName());
            System.out.println("MessageService Bean: "
                    + messageService.getClass().getSimpleName());
            System.out.println("ReadStatusService Bean: "
                    + readStatusService.getClass().getSimpleName());
            System.out.println("UserStatusService Bean: "
                    + userStatusService.getClass().getSimpleName());
            System.out.println("BinaryContentService Bean: "
                    + binaryContentService.getClass().getSimpleName());

            // CRUD 및 기능 테스트
            userCrudTest(userService);
            duplicateUserEmailTest(userService);
            authLoginTest(userService, authService);
            channelCrudTest(channelService);
            channelJoinLeaveTest(userService, channelService);
            messageCrudTest(userService, channelService, messageService);
            readStatusCrudTest(userService, channelService, readStatusService);
            userStatusCrudTest(userService, userStatusService);
            binaryContentCrudTest(binaryContentService);

            System.out.println("\n=== 모든 테스트 완료 ===");
        }
    }

    private static String createTestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static UserCreateRequest createUserRequest(
            String username,
            String email
    ) {
        return new UserCreateRequest(
                username,
                email,
                "password123",
                null,
                null,
                null
        );
    }

    private static void userCrudTest(UserService userService) {
        String testId = createTestId();

        System.out.println("\n=== 사용자 CRUD 테스트 ===");

        UserDto created = userService.create(
                createUserRequest(
                        "테스트 사용자 " + testId,
                        "user-" + testId + "@example.com"
                )
        );

        System.out.println("사용자 등록 완료: " + created.username());

        UserDto found = userService.findById(created.id())
                .orElseThrow(() -> new AssertionError("사용자 조회 실패"));

        if (!found.id().equals(created.id())) {
            throw new AssertionError("사용자 조회 테스트 실패");
        }
        System.out.println("사용자 조회 테스트 성공");

        UserDto updated = userService.update(
                new UserUpdateRequest(
                        created.id(),
                        "수정된 사용자 " + testId,
                        "user-" + testId + "@example.com",
                        "password123",
                        null,
                        null,
                        null
                )
        );

        if (!updated.username().equals("수정된 사용자 " + testId)) {
            throw new AssertionError("사용자 수정 테스트 실패");
        }
        System.out.println("사용자 수정 테스트 성공");

        userService.deleteById(created.id());

        if (userService.findById(created.id()).isPresent()) {
            throw new AssertionError("사용자 삭제 테스트 실패");
        }
        System.out.println("사용자 삭제 테스트 성공");
    }

    private static void duplicateUserEmailTest(UserService userService) {
        String testId = createTestId();
        String email = "duplicate-" + testId + "@example.com";

        System.out.println("\n=== 중복 이메일 검증 테스트 ===");

        userService.create(
                createUserRequest("중복 테스트 사용자 1-" + testId, email)
        );

        try {
            userService.create(
                    createUserRequest("중복 테스트 사용자 2-" + testId, email)
            );

            throw new AssertionError("중복 이메일 등록이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().contains("이메일")) {
                throw e;
            }

            System.out.println("중복 이메일 검증 성공: " + e.getMessage());
        }
    }

    private static void authLoginTest(
            UserService userService,
            AuthService authService
    ) {
        String testId = createTestId();

        System.out.println("\n=== 로그인 테스트 ===");

        String username = "로그인 테스트 사용자 " + testId;
        String password = "password123";

        // 테스트 사용자 생성
        UserDto created = userService.create(
                new UserCreateRequest(
                        username,
                        "login-" + testId + "@example.com",
                        password,
                        null,
                        null,
                        null
                )
        );

        // 1. 정상 로그인
        UserDto loggedIn = authService.login(
                new LoginRequest(username, password)
        );

        if (!loggedIn.id().equals(created.id())) {
            throw new AssertionError("로그인 사용자 정보가 일치하지 않습니다.");
        }

        System.out.println("정상 로그인 테스트 성공");

        // 2. 잘못된 비밀번호
        try {
            authService.login(
                    new LoginRequest(username, "wrongPassword")
            );

            throw new AssertionError("잘못된 비밀번호 로그인이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 비밀번호 검증 성공: " + e.getMessage());
        }

        // 3. 존재하지 않는 사용자
        try {
            authService.login(
                    new LoginRequest(
                            "없는 사용자 " + testId,
                            password
                    )
            );

            throw new AssertionError("존재하지 않는 사용자 로그인이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("존재하지 않는 사용자 검증 성공: " + e.getMessage());
        }

        // 4. 비밀번호가 반환 DTO에 포함되지 않는지 확인
        System.out.println("로그인 사용자: " + loggedIn.username());
        System.out.println("비밀번호 제외 DTO 반환 확인");
    }

    private static void channelCrudTest(ChannelService channelService) {
        String testId = createTestId();

        System.out.println("\n=== 채널 CRUD 테스트 ===");

        ChannelDto created = channelService.createPublic(
                new ChannelCreateRequest(
                        "테스트 채널 " + testId,
                        "채널 CRUD 테스트"
                )
        );

        System.out.println("채널 등록 완료: " + created.name());

        ChannelDto found = channelService.findById(created.id())
                .orElseThrow(() -> new AssertionError("채널 조회 실패"));

        if (!found.id().equals(created.id())) {
            throw new AssertionError("채널 조회 테스트 실패");
        }
        System.out.println("채널 조회 테스트 성공");

        ChannelDto updated = channelService.update(
                new ChannelUpdateRequest(
                        created.id(),
                        "수정된 채널 " + testId,
                        "수정된 설명"
                )
        );

        if (!updated.name().equals("수정된 채널 " + testId)) {
            throw new AssertionError("채널 수정 테스트 실패");
        }
        System.out.println("채널 수정 테스트 성공");

        channelService.delete(created.id());

        if (channelService.findById(created.id()).isPresent()) {
            throw new AssertionError("채널 삭제 테스트 실패");
        }
        System.out.println("채널 삭제 테스트 성공");
    }

    private static void channelJoinLeaveTest(
            UserService userService,
            ChannelService channelService
    ) {
        String testId = createTestId();

        System.out.println("\n=== 채널 참여 및 나가기 테스트 ===");

        UserDto host = userService.create(
                createUserRequest(
                        "채널 생성 사용자 " + testId,
                        "host-" + testId + "@example.com"
                )
        );

        UserDto user = userService.create(
                createUserRequest(
                        "채널 참여 테스트 사용자 " + testId,
                        "join-" + testId + "@example.com"
                )
        );

        ChannelDto channel = channelService.createPrivate(
                new PrivateChannelCreateRequest(List.of(host.id()))
        );

        channelService.join(channel.id(), user.id());

        try {
            channelService.join(channel.id(), user.id());
            throw new AssertionError("중복 참여가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 채널 참여 검증 성공: " + e.getMessage());
        }

        ChannelDto latestChannel = channelService.findById(channel.id())
                .orElseThrow(() -> new AssertionError("채널 조회 실패"));

        if (!latestChannel.participantIds().contains(user.id())) {
            throw new AssertionError("채널 참여 테스트 실패");
        }
        System.out.println("채널 참여 테스트 성공");

        channelService.leave(channel.id(), user.id());

        latestChannel = channelService.findById(channel.id())
                .orElseThrow(() -> new AssertionError("채널 조회 실패"));

        if (latestChannel.participantIds().contains(user.id())) {
            throw new AssertionError("채널 나가기 테스트 실패");
        }
        System.out.println("채널 나가기 테스트 성공");

        try {
            channelService.leave(channel.id(), user.id());
            throw new AssertionError("미참여 채널 나가기가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("미참여 채널 나가기 검증 성공: " + e.getMessage());
        }
    }

    private static void messageCrudTest(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        String testId = createTestId();

        System.out.println("\n=== 메시지 CRUD 테스트 ===");

        UserDto user = userService.create(
                createUserRequest(
                        "메시지 테스트 사용자 " + testId,
                        "message-" + testId + "@example.com"
                )
        );

        ChannelDto channel = channelService.createPublic(
                new ChannelCreateRequest(
                        "메시지 테스트 채널 " + testId,
                        "메시지 CRUD 테스트"
                )
        );

        Message created = messageService.create(
                new MessageCreateRequest(
                        "테스트 메시지 " + testId,
                        user.id(),
                        channel.id(),
                        List.of()
                )
        );

        System.out.println("메시지 등록 완료");

        Message found = messageService.findById(created.getId())
                .orElseThrow(() -> new AssertionError("메시지 조회 실패"));

        if (!found.getId().equals(created.getId())) {
            throw new AssertionError("메시지 조회 테스트 실패");
        }
        System.out.println("메시지 조회 테스트 성공");

        messageService.update(
                new MessageUpdateRequest(
                        created.getId(),
                        "수정된 메시지 " + testId
                )
        );

        Message updated = messageService.findById(created.getId())
                .orElseThrow(() -> new AssertionError("수정 메시지 조회 실패"));

        if (!updated.getContent().equals("수정된 메시지 " + testId)) {
            throw new AssertionError("메시지 수정 테스트 실패");
        }
        System.out.println("메시지 수정 테스트 성공");

        messageService.delete(created.getId());

        if (messageService.findById(created.getId()).isPresent()) {
            throw new AssertionError("메시지 삭제 테스트 실패");
        }
        System.out.println("메시지 삭제 테스트 성공");
    }

    private static void readStatusCrudTest(
            UserService userService,
            ChannelService channelService,
            ReadStatusService readStatusService
    ) {
        String testId = createTestId();

        System.out.println("\n=== 읽음 상태 CRUD 테스트 ===");

        UserDto user = userService.create(
                createUserRequest(
                        "읽음 상태 테스트 사용자 " + testId,
                        "read-status-" + testId + "@example.com"
                )
        );

        ChannelDto channel = channelService.createPublic(
                new ChannelCreateRequest(
                        "읽음 상태 테스트 채널 " + testId,
                        "읽음 상태 CRUD 테스트"
                )
        );

        ReadStatus created = readStatusService.create(
                new ReadStatusCreateRequest(user.id(), channel.id())
        );

        System.out.println("읽음 상태 등록 완료");

        ReadStatus found = readStatusService.findById(created.getId())
                .orElseThrow(() -> new AssertionError("읽음 상태 조회 실패"));

        if (!found.getId().equals(created.getId())) {
            throw new AssertionError("읽음 상태 조회 테스트 실패");
        }
        System.out.println("읽음 상태 조회 테스트 성공");

        List<ReadStatus> userReadStatuses =
                readStatusService.findAllByUserId(user.id());

        if (userReadStatuses.stream()
                .noneMatch(status -> status.getId().equals(created.getId()))) {
            throw new AssertionError("사용자별 읽음 상태 조회 실패");
        }
        System.out.println("사용자별 읽음 상태 조회 테스트 성공");

        try {
            readStatusService.create(
                    new ReadStatusCreateRequest(user.id(), channel.id())
            );
            throw new AssertionError("중복 읽음 상태 생성이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 읽음 상태 검증 성공: " + e.getMessage());
        }

        Instant updatedTime = Instant.parse("2025-01-01T00:00:00Z");

        readStatusService.update(
                new ReadStatusUpdateRequest(created.getId(), updatedTime)
        );

        ReadStatus updated = readStatusService.findById(created.getId())
                .orElseThrow(() -> new AssertionError("수정된 읽음 상태 조회 실패"));

        if (!updated.getLastReadAt().equals(updatedTime)) {
            throw new AssertionError("읽음 상태 수정 테스트 실패");
        }
        System.out.println("읽음 상태 수정 테스트 성공");

        readStatusService.delete(created.getId());

        if (readStatusService.findById(created.getId()).isPresent()) {
            throw new AssertionError("읽음 상태 삭제 테스트 실패");
        }
        System.out.println("읽음 상태 삭제 테스트 성공");
    }

    private static void userStatusCrudTest(
            UserService userService,
            UserStatusService userStatusService
    ) {
        String testId = createTestId();

        System.out.println("\n=== 사용자 접속 상태 CRUD 테스트 ===");

        UserDto user = userService.create(
                createUserRequest(
                        "접속 상태 테스트 사용자 " + testId,
                        "user-status-" + testId + "@example.com"
                )
        );

        UserStatus created = userStatusService.findAll().stream()
                .filter(status -> status.getUserId().equals(user.id()))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("사용자 접속 상태 자동 생성 실패")
                );

        System.out.println("사용자 접속 상태 자동 생성 성공");

        UserStatus found = userStatusService.findById(created.getId())
                .orElseThrow(() ->
                        new AssertionError("사용자 접속 상태 조회 실패")
                );

        if (!found.getId().equals(created.getId())) {
            throw new AssertionError("사용자 접속 상태 조회 테스트 실패");
        }
        System.out.println("사용자 접속 상태 조회 테스트 성공");

        try {
            userStatusService.create(new UserStatusCreateRequest(user.id()));
            throw new AssertionError("중복 사용자 접속 상태 생성이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 생성 검증 성공: " + e.getMessage());
        }

        Instant updatedTime = Instant.parse("2025-01-01T00:00:00Z");

        userStatusService.update(
                new UserStatusUpdateRequest(created.getId(), updatedTime)
        );

        UserStatus updated = userStatusService.findById(created.getId())
                .orElseThrow(() ->
                        new AssertionError("수정된 사용자 접속 상태 조회 실패")
                );

        if (!updated.getLastActiveAt().equals(updatedTime)) {
            throw new AssertionError("사용자 접속 상태 수정 테스트 실패");
        }
        System.out.println("사용자 접속 상태 수정 테스트 성공");

        Instant updatedTimeByUserId = Instant.parse("2025-02-01T00:00:00Z");

        userStatusService.updateByUserId(
                user.id(),
                new UserStatusUpdateRequest(
                        created.getId(),
                        updatedTimeByUserId
                )
        );

        UserStatus updatedByUserId = userStatusService.findById(created.getId())
                .orElseThrow(() ->
                        new AssertionError("사용자 ID 기준 수정 후 조회 실패")
                );

        if (!updatedByUserId.getLastActiveAt().equals(updatedTimeByUserId)) {
            throw new AssertionError("사용자 ID 기준 수정 테스트 실패");
        }
        System.out.println("사용자 ID 기준 수정 테스트 성공");

        userStatusService.delete(created.getId());

        if (userStatusService.findById(created.getId()).isPresent()) {
            throw new AssertionError("사용자 접속 상태 삭제 테스트 실패");
        }
        System.out.println("사용자 접속 상태 삭제 테스트 성공");
    }

    private static void binaryContentCrudTest(
            BinaryContentService binaryContentService
    ) {
        System.out.println("\n=== 첨부파일 CRUD 테스트 ===");

        byte[] imageData = new byte[1024];

        BinaryContentCreateRequest createRequest =
                new BinaryContentCreateRequest(
                        "test-image.png",
                        "image/png",
                        imageData.length,
                        imageData
                );

        BinaryContent created =
                binaryContentService.create(createRequest);

        System.out.println("첨부파일 생성: " + created.getFileName());

        BinaryContent found = binaryContentService.findById(created.getId())
                .orElseThrow(() ->
                        new AssertionError("첨부파일 조회 실패")
                );

        if (!found.getId().equals(created.getId())) {
            throw new AssertionError("첨부파일 조회 테스트 실패");
        }
        System.out.println("첨부파일 조회 테스트 성공");

        List<BinaryContent> contents =
                binaryContentService.findAllByIdIn(List.of(created.getId()));

        if (contents.size() != 1
                || !contents.get(0).getId().equals(created.getId())) {
            throw new AssertionError("첨부파일 목록 조회 테스트 실패");
        }
        System.out.println("첨부파일 목록 조회 테스트 성공");

        binaryContentService.deleteById(created.getId());

        boolean deleted = binaryContentService.findById(created.getId()).isEmpty();

        if (!deleted) {
            throw new AssertionError("첨부파일 삭제 테스트 실패");
        }
        System.out.println("첨부파일 삭제 테스트 성공");
    }
}