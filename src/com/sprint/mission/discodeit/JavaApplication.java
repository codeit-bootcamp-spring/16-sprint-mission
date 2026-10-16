package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {

        // 서비스 가져오기
        UserService userService = ServiceFactory.userService();
        ChannelService channelService = ServiceFactory.channelService();
        MessageService messageService = ServiceFactory.messageService();

        // 2. 사용자 CRUD 테스트
        System.out.println("\n=== 사용자 CRUD 테스트 ===");

        User user1 = new User("민영", "minyeong@example.com");
        User user2 = new User("민석", "minseok@example.com");

        userService.create(user1);
        userService.create(user2);

        System.out.println("사용자 등록 완료");
        System.out.println("사용자 조회: "
                + userService.findById(user1.getId()).get().getName());

        System.out.println("전체 사용자 목록");
        for (User user : userService.findAll()) {
            System.out.println(user.getName() + " / " + user.getEmail());
        }

        userService.update(
                user1.getId(),
                "민영 수정",
                "minyeong@example.com"
        );

        System.out.println("수정 후 사용자: "
                + userService.findById(user1.getId()).get().getName());

        userService.deleteById(user2.getId());

        if (!userService.findById(user2.getId()).isPresent()) {
            System.out.println("사용자 삭제 테스트 성공");
        } else {
            throw new AssertionError("사용자 삭제 테스트 실패");
        }
        // 중복 사용자 이메일 테스트
        System.out.println("\n=== 중복 사용자 이메일 테스트 ===");

        userService.create(
                new User("테스트 사용자 1", "duplicate@example.com")
        );

        try {
            userService.create(
                    new User("테스트 사용자 2", "duplicate@example.com")
            );

            throw new AssertionError("중복 이메일 등록이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 이메일 검증 성공: " + e.getMessage());
        }

        // 3. 채널 CRUD 테스트
        System.out.println("\n=== 채널 CRUD 테스트 ===");

        Channel channel1 = channelService.create(
                "자유 채팅",
                "자유롭게 이야기하는 채널"
        );

        System.out.println("\n=== 채널 참여 및 나가기 테스트 ===");

        // 채널 참여
        channelService.join(channel1.getId(), user1.getId());

        try {
            channelService.join(channel1.getId(), user1.getId());
            throw new AssertionError("중복 참여가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 채널 참여 검증 성공: " + e.getMessage());
        }

        if (channel1.hasUser(user1) && user1.hasChannel(channel1)) {
            System.out.println("채널 참여 테스트 성공");
        } else {
            throw new AssertionError("채널 참여 테스트 실패");
        }

        // 채널 나가기
        channelService.leave(channel1.getId(), user1.getId());

        if (!channel1.hasUser(user1) && !user1.hasChannel(channel1)) {
            System.out.println("채널 나가기 테스트 성공");
        } else {
            throw new AssertionError("채널 나가기 테스트 실패");
        }
        // 참여하지 않은 채널에서 나가기 테스트
        try {
            channelService.leave(channel1.getId(), user1.getId());
            throw new AssertionError("참여하지 않은 채널에서 나가기가 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("미참여 채널 나가기 검증 성공: " + e.getMessage());
        }

        System.out.println("채널 등록 완료");
        System.out.println(
                "채널 조회: " + channelService.findById(channel1.getId())
                        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."))
                        .getName()
        );

        System.out.println("전체 채널 목록");
        for (Channel channel : channelService.findAll()) {
            System.out.println(channel.getName());
        }

        channelService.update(
                channel1.getId(),
                "공지 채널",
                "공지사항을 전달하는 채널"
        );

        System.out.println(
                "수정 후 채널: " + channelService.findById(channel1.getId())
                        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."))
                        .getName()
        );

        channelService.delete(channel1.getId());

        if (channelService.findById(channel1.getId()).isEmpty()) {
            System.out.println("채널 삭제 테스트 성공");
        } else {
            throw new AssertionError("채널 삭제 테스트 실패");
        }

        // 채널 삭제 후 사용자 관계 제거 테스트
        User relationUser = new User(
                "관계 테스트 사용자",
                "relation@example.com"
        );
        userService.create(relationUser);

        Channel relationChannel = channelService.create(
                "관계 테스트 채널",
                "채널 삭제 관계 테스트"
        );
        // 사용자 채널 참여
        channelService.join(
                relationChannel.getId(),
                relationUser.getId()
        );

        // 삭제 전 관계 확인
        if (!relationUser.hasChannel(relationChannel)
                || !relationChannel.hasUser(relationUser)) {
            throw new AssertionError("삭제 전 채널 관계 테스트 실패");
        }
        // 채널 삭제
        channelService.delete(relationChannel.getId());

        // 삭제 후 관계 확인
        System.out.println(
                "사용자 채널 목록에서 제거: "
                        + !relationUser.hasChannel(relationChannel)
        );

        System.out.println(
                "채널 사용자 목록에서 제거: "
                        + !relationChannel.hasUser(relationUser)
        );

        System.out.println(
                "채널 저장소에서 삭제: "
                        + channelService.findById(relationChannel.getId()).isEmpty()
        );

        if (!relationUser.hasChannel(relationChannel)
                && !relationChannel.hasUser(relationUser)
                && channelService.findById(relationChannel.getId()).isEmpty()) {
            System.out.println("채널 삭제 후 사용자 관계 제거 테스트 성공");
        } else {
            throw new AssertionError("채널 삭제 후 사용자 관계 제거 테스트 실패");
        }
        // 중복 채널 이름 테스트
        System.out.println("\n=== 중복 채널 이름 테스트 ===");

        channelService.create(
                "중복 테스트 채널",
                "첫 번째 채널"
        );

        try {
            channelService.create(
                    "중복 테스트 채널",
                    "두 번째 채널"
            );

            throw new AssertionError("중복 채널 이름 등록이 허용되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 채널 이름 검증 성공: " + e.getMessage());
        }

        // 4. 메시지 CRUD 테스트
        System.out.println("\n=== 메시지 CRUD 테스트 ===");

        User messageUser = new User(
                "테스트 사용자",
                "test@example.com"
        );
        userService.create(messageUser);

        Channel messageChannel = channelService.create(
                "메시지 테스트 채널",
                "메시지를 테스트하기 위한 채널"
        );

        Message message = messageService.create(
                "첫 번째 메시지",
                messageUser.getId(),
                messageChannel.getId()
        );
        // 메시지와 사용자·채널의 관계 테스트
        if (messageUser.getMessages().contains(message)
                && messageChannel.getMessages().contains(message)) {
            System.out.println("메시지 관계 테스트 성공");
        } else {
            throw new AssertionError("메시지 관계 테스트 실패");
        }

        System.out.println("메시지 등록 완료");
        System.out.println("메시지 조회: "
                + messageService.findById(message.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."))
                .getContent());


        System.out.println("전체 메시지 목록");
        for (Message item : messageService.findAll()) {
            System.out.println(item.getContent());
        }

        messageService.update(
                message.getId(),
                "수정된 메시지"
        );

        System.out.println("수정 후 메시지: "
                + messageService.findById(message.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."))
                .getContent());

        messageService.delete(message.getId());

        if (messageService.findById(message.getId()).isEmpty()
                && !messageUser.getMessages().contains(message)
                && !messageChannel.getMessages().contains(message)) {
            System.out.println("메시지 삭제 및 관계 제거 테스트 성공");
        } else {
            throw new AssertionError("메시지 삭제 및 관계 제거 테스트 실패");
        }
        // 채널 삭제 시 연결된 메시지 처리 테스트
        System.out.println("\n=== 채널 삭제 시 메시지 처리 테스트 ===");

        User deleteTestUser = new User(
                "채널 삭제 테스트 사용자",
                "channel-delete@example.com"
        );
        userService.create(deleteTestUser);

        Channel deleteTestChannel = channelService.create(
                "채널 삭제 테스트",
                "메시지 삭제 확인용 채널"
        );

        Message deleteTestMessage = messageService.create(
                "채널 삭제 시 함께 처리할 메시지",
                deleteTestUser.getId(),
                deleteTestChannel.getId()
        );

        // 채널 삭제
        channelService.delete(deleteTestChannel.getId());

        // 채널 삭제 후 메시지가 저장소에 남아 있는지 확인
        System.out.println(
                "메시지 저장소에 메시지가 남아 있는가: "
                        + messageService.findById(deleteTestMessage.getId()).isPresent()
        );

        System.out.println(
                "사용자 메시지 목록에 메시지가 남아 있는가: "
                        + deleteTestUser.getMessages().contains(deleteTestMessage)
        );

        // 존재하지 않는 메시지 수정 테스트
        try {
            messageService.update(
                    message.getId(),
                    "수정할 수 없는 메시지"
            );

            throw new AssertionError(
                    "존재하지 않는 메시지 수정이 허용되었습니다."
            );
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "메시지 수정 예외 테스트 성공: " + e.getMessage()
            );
        }
        // 존재하지 않는 메시지 삭제 테스트
        try {
            messageService.delete(message.getId());

            throw new AssertionError(
                    "존재하지 않는 메시지 삭제가 허용되었습니다."
            );
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "메시지 삭제 예외 테스트 성공: " + e.getMessage()
            );
        }

        // 5. 잘못된 사용자 ID 검증
        System.out.println("\n=== 잘못된 사용자 테스트 ===");

        try {
            messageService.create(
                    "잘못된 사용자 메시지",
                    UUID.randomUUID(),
                    messageChannel.getId()
            );

            throw new AssertionError(
                    "잘못된 사용자 ID로 메시지가 생성되었습니다."
            );

        } catch (IllegalArgumentException e) {
            System.out.println("검증 성공: " + e.getMessage());
        }

        // 6. 잘못된 채널 ID 검증
        System.out.println("\n=== 잘못된 채널 테스트 ===");

        try {
            messageService.create(
                    "잘못된 채널 메시지",
                    messageUser.getId(),
                    UUID.randomUUID()
            );

            throw new AssertionError(
                    "잘못된 사용자 ID로 메시지가 생성되었습니다."
            );

        } catch (IllegalArgumentException e) {
            System.out.println("검증 성공: " + e.getMessage());
        }

        System.out.println("\n=== 모든 테스트 종료 ===");
    }
}