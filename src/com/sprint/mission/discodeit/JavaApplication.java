package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        System.out.println("기존 JavaApplication 테스트 코드는");
        System.out.println("DiscodeitApplication에서 Spring Context를 통해 실행합니다.");
    }

    private static String createTestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static void userCrudTest(UserService userService) {
        String testId = createTestId();

        System.out.println("\n=== 사용자 CRUD 테스트 ===");

        UserDto user1 = userService.create(
                new UserCreateRequest(
                        "민영 " + testId,
                        "minyeong-" + testId + "@example.com",
                        "password123", null, null, null
                )
        );

        UserDto user2 = userService.create(
                new UserCreateRequest(
                        "민석 " + testId,
                        "minseok-" + testId + "@example.com",
                        "password123", null, null, null
                )
        );

        System.out.println("사용자 등록 완료");
        System.out.println("사용자 조회: "
                + userService.findById(user1.id()).orElseThrow().username());

        System.out.println("전체 사용자 목록");
        for (UserDto user : userService.findAll()) {
            System.out.println(user.username() + " / " + user.email());
        }

        userService.update(
                new UserUpdateRequest(
                        user1.id(),
                        "민영 수정 " + testId,
                        user1.email(),
                        "password123", null, null, null
                )
        );

        System.out.println("수정 후 사용자: "
                + userService.findById(user1.id()).orElseThrow().username());

        userService.deleteById(user2.id());

        if (userService.findById(user2.id()).isEmpty()) {
            System.out.println("사용자 삭제 테스트 성공");
        } else {
            throw new AssertionError("사용자 삭제 테스트 실패");
        }
    }

    private static void duplicateUserEmailTest(UserService userService) {
        String testId = createTestId();
        String duplicateEmail = "duplicate-" + testId + "@example.com";

        System.out.println("\n=== 중복 사용자 이메일 테스트 ===");

        userService.create(
                new UserCreateRequest(
                        "테스트 사용자 1 " + testId,
                        duplicateEmail,
                        "password123", null, null, null
                )
        );

        try {
            userService.create(
                    new UserCreateRequest(
                            "테스트 사용자 2 " + testId,
                            duplicateEmail,
                            "password123", null, null, null
                    )
            );

            throw new AssertionError("중복 이메일 등록이 허용되었습니다.");

        } catch (IllegalArgumentException e) {
            if (e.getMessage() == null
                    || !e.getMessage().contains("이메일")) {
                throw e;
            }

            System.out.println("중복 이메일 검증 성공: " + e.getMessage());
        }
    }

    private static void channelCrudTest(ChannelService channelService) {
        String testId = createTestId();

        System.out.println("\n=== 채널 CRUD 테스트 ===");

        ChannelDto channel = channelService.createPublic(
                new ChannelCreateRequest(
                        "자유 채팅 " + testId,
                        "자유롭게 이야기하는 채널"
                )
        );

        System.out.println("채널 등록 완료");
        System.out.println("채널 조회: "
                + channelService.findById(channel.id())
                .orElseThrow().name());

        System.out.println("전체 채널 목록");
        for (ChannelDto item : channelService.findAll()) {
            System.out.println(item.name());
        }

        channelService.update(
                new ChannelUpdateRequest(
                        channel.id(),
                        "공지 채널 " + testId,
                        "공지사항을 전달하는 채널"
                )
        );

        System.out.println("수정 후 채널: "
                + channelService.findById(channel.id())
                .orElseThrow().name());

        channelService.delete(channel.id());

        if (channelService.findById(channel.id()).isEmpty()) {
            System.out.println("채널 삭제 테스트 성공");
        } else {
            throw new AssertionError("채널 삭제 테스트 실패");
        }
    }

    private static void channelJoinLeaveTest(
            UserService userService,
            ChannelService channelService
    ) {
        String testId = createTestId();

        System.out.println("\n=== 채널 참여 및 나가기 테스트 ===");

        UserDto user = userService.create(
                new UserCreateRequest(
                        "채널 참여 테스트 사용자 " + testId,
                        "join-" + testId + "@example.com",
                        "password123", null, null, null
                )
        );

        ChannelDto channel = channelService.createPrivate(
                new PrivateChannelCreateRequest(List.of(user.id()))
        );

        channelService.join(channel.id(), user.id());

        try {
            channelService.join(channel.id(), user.id());
            throw new AssertionError("중복 참여가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 채널 참여 검증 성공: " + e.getMessage());
        }

        ChannelDto latestChannel =
                channelService.findById(channel.id()).orElseThrow();

        if (latestChannel.participantIds().contains(user.id())) {
            System.out.println("채널 참여 테스트 성공");
        } else {
            throw new AssertionError("채널 참여 테스트 실패");
        }

        channelService.leave(channel.id(), user.id());

        latestChannel = channelService.findById(channel.id()).orElseThrow();

        if (!latestChannel.participantIds().contains(user.id())) {
            System.out.println("채널 나가기 테스트 성공");
        } else {
            throw new AssertionError("채널 나가기 테스트 실패");
        }

        try {
            channelService.leave(channel.id(), user.id());
            throw new AssertionError("미참여 채널 나가기가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "미참여 채널 나가기 검증 성공: " + e.getMessage()
            );
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
                new UserCreateRequest(
                        "메시지 테스트 사용자 " + testId,
                        "message-" + testId + "@example.com",
                        "password123", null, null, null
                )
        );

        ChannelDto channel = channelService.createPublic(
                new ChannelCreateRequest(
                        "메시지 테스트 채널 " + testId,
                        "메시지 테스트용 채널"
                )
        );

        Message message = messageService.create(
                new MessageCreateRequest(
                        "첫 번째 메시지",
                        user.id(),
                        channel.id(),
                        List.of()
                )
        );

        System.out.println("메시지 등록 완료");

        System.out.println("메시지 조회: "
                + messageService.findById(message.getId())
                .orElseThrow().getContent());

        System.out.println("채널별 메시지 목록");
        for (Message item : messageService.findAllByChannelId(channel.id())) {
            System.out.println(item.getContent());
        }

        messageService.update(
                new MessageUpdateRequest(
                        message.getId(),
                        "수정된 메시지"
                )
        );

        System.out.println("수정 후 메시지: "
                + messageService.findById(message.getId())
                .orElseThrow().getContent());

        messageService.delete(message.getId());

        if (messageService.findById(message.getId()).isEmpty()) {
            System.out.println("메시지 삭제 테스트 성공");
        } else {
            throw new AssertionError("메시지 삭제 테스트 실패");
        }
    }
}