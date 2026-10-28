package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class MessageTestRunner {
    public static void run(
            MessageService messageService,
            ChannelService channelService,
            UserService userService,
            User woody,
            User buzz,
            Channel general,
            Message existingWoodyMessage
    ) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("\n=== 메시지 테스트 ===");

        checkException("다른 사용자의 메시지 수정", "본인이 작성한 메시지만", () ->
                messageService.updateMessage(existingWoodyMessage.getId(), buzz.getId(), "권한 없는 수정"));

        User outsider = new User("outsider-" + suffix, "password123!",
                "outsider-" + suffix + "@example.com");
        userService.createUser(outsider);
        try {
            checkException("비참여자의 메시지 작성", "참여한 유저만", () ->
                    messageService.createMessage(new Message("참여하지 않은 채널 메시지", outsider.getId(), general.getId())));
        } finally {
            userService.deleteUser(outsider.getId(), "password123!");
        }

        Message message = messageService.createMessage(new Message(
                "메시지 CRUD 확인 " + suffix, woody.getId(), general.getId()));
        ensure(userService.getUserById(woody.getId()).getMyMessageIds().contains(message.getId()),
                "작성자의 메시지 목록에 새 메시지가 없습니다.");
        ensure(channelService.getChannelById(general.getId()).getMessageIds().contains(message.getId()),
                "채널 메시지 목록에 새 메시지가 없습니다.");
        System.out.println("메시지 생성 후 작성자·채널 관계 확인: " + message.getContent());

        String updatedContent = "수정된 메시지 " + suffix;
        messageService.updateMessage(message.getId(), woody.getId(), updatedContent);
        Message updated = messageService.getMessageById(message.getId());
        ensure(updatedContent.equals(updated.getContent()), "메시지 수정 후 재조회 실패");
        System.out.println("메시지 수정 후 재조회: " + updated.getContent());

        messageService.deleteMessage(message.getId(), woody.getId());
        ensure(messageService.getAllMessages().stream().noneMatch(saved -> saved.getId().equals(message.getId())),
                "삭제된 메시지가 저장소에 남아 있습니다.");
        ensure(!userService.getUserById(woody.getId()).getMyMessageIds().contains(message.getId()),
                "삭제된 메시지가 작성자의 목록에 남아 있습니다.");
        ensure(!channelService.getChannelById(general.getId()).getMessageIds().contains(message.getId()),
                "삭제된 메시지가 채널 목록에 남아 있습니다.");
        System.out.println("메시지 삭제 후 저장소·작성자·채널 관계 확인");
    }

    private static void checkException(String name, String expected, Runnable action) {
        try {
            action.run();
            throw new IllegalStateException(name + " 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage() == null || !e.getMessage().contains(expected)) throw e;
            System.out.println(name + " 검증 성공: " + e.getMessage());
        }
    }

    private static void ensure(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
