package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {

        // 1. Basic 서비스 초기화 및 Repository 의존성 주입
        BasicUserService basicUserService =
                new BasicUserService(new FileUserRepository());

        BasicChannelService basicChannelService =
                new BasicChannelService(
                        new FileChannelRepository(),
                        basicUserService
                );

        BasicMessageService basicMessageService =
                new BasicMessageService(
                        new FileMessageRepository(),
                        basicUserService,
                        basicChannelService
                );

        // 2. 서비스 간 의존성 연결
        basicUserService.setMessageService(basicMessageService);
        basicUserService.setChannelService(basicChannelService);
        basicChannelService.setMessageService(basicMessageService);

        // 3. 서비스 인터페이스로 사용
        UserService userService = basicUserService;
        ChannelService channelService = basicChannelService;
        MessageService messageService = basicMessageService;

        // 실행할 때마다 이메일과 채널 이름이 겹치지 않도록 고유한 값 사용
        String testId = UUID.randomUUID().toString().substring(0, 8);

        System.out.println("\n=== 사용자 CRUD 테스트 ===");

        User user1 = userService.create(
                new User(
                        "민영 " + testId,
                        "minyeong-" + testId + "@example.com"
                )
        );

        User user2 = userService.create(
                new User(
                        "민석 " + testId,
                        "minseok-" + testId + "@example.com"
                )
        );

        System.out.println("사용자 등록 완료");
        System.out.println("사용자 조회: "
                + userService.findById(user1.getId()).orElseThrow().getName());

        System.out.println("전체 사용자 목록");
        for (User user : userService.findAll()) {
            System.out.println(user.getName() + " / " + user.getEmail());
        }

        userService.update(
                user1.getId(),
                "민영 수정 " + testId,
                user1.getEmail()
        );

        System.out.println("수정 후 사용자: "
                + userService.findById(user1.getId()).orElseThrow().getName());

        userService.deleteById(user2.getId());

        if (userService.findById(user2.getId()).isEmpty()) {
            System.out.println("사용자 삭제 테스트 성공");
        } else {
            throw new AssertionError("사용자 삭제 테스트 실패");
        }

        System.out.println("\n=== 중복 사용자 이메일 테스트 ===");

        String duplicateEmail = "duplicate-" + testId + "@example.com";

        userService.create(new User("테스트 사용자 1", duplicateEmail));

        try {
            userService.create(new User("테스트 사용자 2", duplicateEmail));
            throw new AssertionError("중복 이메일 등록이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 이메일 검증 성공: " + e.getMessage());
        }

        System.out.println("\n=== 채널 CRUD 테스트 ===");

        Channel channel1 = channelService.create(
                "자유 채팅 " + testId,
                "자유롭게 이야기하는 채널"
        );

        System.out.println("\n=== 채널 참여 및 나가기 테스트 ===");

        channelService.join(channel1.getId(), user1.getId());

        try {
            channelService.join(channel1.getId(), user1.getId());
            throw new AssertionError("중복 참여가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 채널 참여 검증 성공: " + e.getMessage());
        }

        Channel latestChannel =
                channelService.findById(channel1.getId()).orElseThrow();

        User latestUser =
                userService.findById(user1.getId()).orElseThrow();

        if (latestChannel.hasMember(latestUser.getId())
                && latestUser.hasChannel(latestChannel.getId())) {
            System.out.println("채널 참여 테스트 성공");
        } else {
            throw new AssertionError("채널 참여 테스트 실패");
        }

        channelService.leave(channel1.getId(), user1.getId());

        latestChannel =
                channelService.findById(channel1.getId()).orElseThrow();

        latestUser =
                userService.findById(user1.getId()).orElseThrow();

        if (!latestChannel.hasMember(latestUser.getId())
                && !latestUser.hasChannel(latestChannel.getId())) {
            System.out.println("채널 나가기 테스트 성공");
        } else {
            throw new AssertionError("채널 나가기 테스트 실패");
        }

        try {
            channelService.leave(channel1.getId(), user1.getId());
            throw new AssertionError("참여하지 않은 채널에서 나가기가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("미참여 채널 나가기 검증 성공: " + e.getMessage());
        }

        System.out.println("채널 등록 완료");
        System.out.println("채널 조회: "
                + channelService.findById(channel1.getId()).orElseThrow().getName());

        System.out.println("전체 채널 목록");
        for (Channel channel : channelService.findAll()) {
            System.out.println(channel.getName());
        }

        channelService.update(
                channel1.getId(),
                "공지 채널 " + testId,
                "공지사항을 전달하는 채널"
        );

        System.out.println("수정 후 채널: "
                + channelService.findById(channel1.getId()).orElseThrow().getName());

        channelService.delete(channel1.getId());

        if (channelService.findById(channel1.getId()).isEmpty()) {
            System.out.println("채널 삭제 테스트 성공");
        } else {
            throw new AssertionError("채널 삭제 테스트 실패");
        }

        System.out.println("\n=== 채널 삭제 후 사용자 관계 제거 테스트 ===");

        User relationUser = userService.create(
                new User(
                        "관계 테스트 사용자 " + testId,
                        "relation-" + testId + "@example.com"
                )
        );

        Channel relationChannel = channelService.create(
                "관계 테스트 채널 " + testId,
                "채널 삭제 관계 테스트"
        );

        channelService.join(relationChannel.getId(), relationUser.getId());

        relationChannel =
                channelService.findById(relationChannel.getId()).orElseThrow();

        relationUser =
                userService.findById(relationUser.getId()).orElseThrow();

        if (!relationUser.hasChannel(relationChannel.getId())
                || !relationChannel.hasMember(relationUser.getId())) {
            throw new AssertionError("삭제 전 채널 관계 테스트 실패");
        }

        UUID relationChannelId = relationChannel.getId();
        UUID relationUserId = relationUser.getId();

        channelService.delete(relationChannelId);

        User userAfterChannelDelete =
                userService.findById(relationUserId).orElseThrow();

        boolean userRelationshipRemoved =
                !userAfterChannelDelete.hasChannel(relationChannelId);

        boolean channelRemoved =
                channelService.findById(relationChannelId).isEmpty();

        System.out.println("저장된 사용자 채널 목록에서 제거: "
                + userRelationshipRemoved);
        System.out.println("채널 저장소에서 삭제: " + channelRemoved);

        if (userRelationshipRemoved && channelRemoved) {
            System.out.println("채널 삭제 후 사용자 관계 제거 테스트 성공");
        } else {
            throw new AssertionError("채널 삭제 후 사용자 관계 제거 테스트 실패");
        }

        System.out.println("\n=== 중복 채널 이름 테스트 ===");

        String duplicateChannelName = "중복 테스트 채널 " + testId;

        channelService.create(duplicateChannelName, "첫 번째 채널");

        try {
            channelService.create(duplicateChannelName, "두 번째 채널");
            throw new AssertionError("중복 채널 이름 등록이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 채널 이름 검증 성공: " + e.getMessage());
        }

        System.out.println("\n=== 메시지 CRUD 테스트 ===");

        User messageUser = userService.create(
                new User(
                        "메시지 테스트 사용자 " + testId,
                        "message-" + testId + "@example.com"
                )
        );

        Channel messageChannel = channelService.create(
                "메시지 테스트 채널 " + testId,
                "메시지를 테스트하기 위한 채널"
        );

        Message message = messageService.create(
                "첫 번째 메시지",
                messageUser.getId(),
                messageChannel.getId()
        );

        messageUser = userService.findById(messageUser.getId()).orElseThrow();
        messageChannel =
                channelService.findById(messageChannel.getId()).orElseThrow();

        UUID userId = messageUser.getId();
        UUID channelId = messageChannel.getId();
        UUID messageId = message.getId();

        boolean userHasMessage = messageService.findAll().stream()
                .anyMatch(item -> item.getId().equals(messageId)
                        && item.getAuthorId().equals(userId));

        boolean channelHasMessage = messageService.findAll().stream()
                .anyMatch(item -> item.getId().equals(messageId)
                        && item.getChannelId().equals(channelId));

        if (userHasMessage && channelHasMessage) {
            System.out.println("메시지 관계 테스트 성공");
        } else {
            throw new AssertionError("메시지 관계 테스트 실패");
        }

        System.out.println("메시지 등록 완료");
        System.out.println("메시지 조회: "
                + messageService.findById(message.getId()).orElseThrow().getContent());

        System.out.println("전체 메시지 목록");
        for (Message item : messageService.findAll()) {
            System.out.println(item.getContent());
        }

        messageService.update(message.getId(), "수정된 메시지");

        System.out.println("수정 후 메시지: "
                + messageService.findById(message.getId()).orElseThrow().getContent());

        messageService.delete(message.getId());

        boolean userStillHasMessage = messageService.findAll().stream()
                .anyMatch(item -> item.getId().equals(messageId)
                        && item.getAuthorId().equals(userId));

        boolean channelStillHasMessage = messageService.findAll().stream()
                .anyMatch(item -> item.getId().equals(messageId)
                        && item.getChannelId().equals(channelId));

        if (messageService.findById(messageId).isEmpty()
                && !userStillHasMessage
                && !channelStillHasMessage) {
            System.out.println("메시지 삭제 및 관계 제거 테스트 성공");
        } else {
            throw new AssertionError("메시지 삭제 및 관계 제거 테스트 실패");
        }

        System.out.println("\n=== 채널 삭제 시 메시지 처리 테스트 ===");

        User deleteTestUser = userService.create(
                new User(
                        "채널 삭제 테스트 사용자 " + testId,
                        "channel-delete-" + testId + "@example.com"
                )
        );

        Channel deleteTestChannel = channelService.create(
                "채널 삭제 테스트 " + testId,
                "메시지 삭제 확인용 채널"
        );

        Message deleteTestMessage = messageService.create(
                "채널 삭제 시 함께 처리할 메시지",
                deleteTestUser.getId(),
                deleteTestChannel.getId()
        );

        channelService.delete(deleteTestChannel.getId());

        boolean messageStillInRepository =
                messageService.findById(deleteTestMessage.getId()).isPresent();

        boolean messageStillInChannel = messageService.findAll().stream()
                .anyMatch(item -> item.getId().equals(deleteTestMessage.getId())
                        && item.getChannelId().equals(deleteTestChannel.getId()));

        System.out.println("메시지 저장소에 메시지가 남아 있는가: "
                + messageStillInRepository);
        System.out.println("삭제된 채널의 메시지가 남아 있는가: "
                + messageStillInChannel);

        if (!messageStillInRepository && !messageStillInChannel) {
            System.out.println("채널 삭제 시 메시지 처리 테스트 성공");
        } else {
            throw new AssertionError("채널 삭제 시 메시지 처리 테스트 실패");
        }

        System.out.println("\n=== 존재하지 않는 메시지 수정 테스트 ===");

        try {
            messageService.update(message.getId(), "수정할 수 없는 메시지");
            throw new AssertionError("존재하지 않는 메시지 수정이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("메시지 수정 예외 테스트 성공: " + e.getMessage());
        }

        System.out.println("\n=== 존재하지 않는 메시지 삭제 테스트 ===");

        try {
            messageService.delete(message.getId());
            throw new AssertionError("존재하지 않는 메시지 삭제가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("메시지 삭제 예외 테스트 성공: " + e.getMessage());
        }

        System.out.println("\n=== 잘못된 사용자 테스트 ===");

        try {
            messageService.create(
                    "잘못된 사용자 메시지",
                    UUID.randomUUID(),
                    messageChannel.getId()
            );

            throw new AssertionError("잘못된 사용자 ID로 메시지가 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("검증 성공: " + e.getMessage());
        }

        System.out.println("\n=== 잘못된 채널 테스트 ===");

        try {
            messageService.create(
                    "잘못된 채널 메시지",
                    messageUser.getId(),
                    UUID.randomUUID()
            );

            throw new AssertionError("잘못된 채널 ID로 메시지가 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("검증 성공: " + e.getMessage());
        }

        System.out.println("\n=== 모든 테스트 종료 ===");
    }
}