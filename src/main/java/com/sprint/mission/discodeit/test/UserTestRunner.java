package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class UserTestRunner {
    public static void run(
            UserService userService,
            ChannelService channelService,
            MessageService messageService,
            User woody
    ) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User savedWoody = userService.getUserById(woody.getId());
        String oldName = savedWoody.getUsername();
        String oldEmail = savedWoody.getEmail();
        String oldPassword = savedWoody.getPassword();

        System.out.println("\n=== 사용자 테스트 ===");
        try {
            userService.updateUsername(woody.getId(), oldName + "-수정");
            userService.updateEmail(woody.getId(), "woody-test-" + suffix + "@example.com");
            userService.updatePassword(woody.getId(), oldPassword, "temporary-" + suffix);
            User updated = userService.getUserById(woody.getId());
            System.out.println("사용자 수정 후 재조회: " + updated.getUsername()
                    + " (" + updated.getEmail() + ")");

            checkException("중복 이메일", "이미 사용 중인 이메일입니다.", () ->
                    userService.createUser(new User("duplicate-email-" + suffix,
                            "password123!", updated.getEmail())));

            // 수정 테스트가 기존 사용자 데이터에 영향을 주지 않도록 원래 값으로 복구한다.
            userService.updateUsername(woody.getId(), oldName);
            userService.updateEmail(woody.getId(), oldEmail);
            userService.updatePassword(woody.getId(), "temporary-" + suffix, oldPassword);

            User deletionUser = new User("탈퇴테스트-" + suffix, "password123!",
                    "delete-user-" + suffix + "@example.com");
            userService.createUser(deletionUser);
            Channel channel = channelService.createChannel(
                    new Channel("사용자 삭제 확인 " + suffix, woody.getId()));
            try {
                channelService.joinChannel(channel.getId(), deletionUser.getId());
                Message relatedMessage = messageService.createMessage(new Message(
                        "삭제 사용자 메시지 " + suffix, deletionUser.getId(), channel.getId()));

                System.out.println("\n사용자 삭제 전 채널:");
                printChannel(channelService.getChannelById(channel.getId()), messageService, userService);

                userService.deleteUser(deletionUser.getId(), "password123!");

                Channel reloadedChannel = channelService.getChannelById(channel.getId());
                boolean userGone = userService.getAllUsers().stream()
                        .noneMatch(user -> user.getId().equals(deletionUser.getId()));
                boolean participantGone = !reloadedChannel.getParticipantIds().contains(deletionUser.getId());
                boolean messageGone = messageService.getAllMessages().stream()
                        .noneMatch(message -> message.getId().equals(relatedMessage.getId()));
                ensure(userGone && participantGone && messageGone,
                        "사용자 삭제 후 연관 데이터가 남아 있습니다.");

                System.out.println("\n사용자 삭제 후 채널:");
                printChannel(reloadedChannel, messageService, userService);
                System.out.println("사용자 삭제 후 사용자·채널·메시지 재조회 확인");
            } finally {
                if (existsUser(userService, deletionUser.getId())) {
                    userService.deleteUser(deletionUser.getId(), "password123!");
                }
                if (existsChannel(channelService, channel.getId())) {
                    channelService.deleteChannel(channel.getId(), woody.getId());
                }
            }
        } finally {
            User latest = userService.getUserById(woody.getId());
            if (!oldName.equals(latest.getUsername())) {
                userService.updateUsername(woody.getId(), oldName);
            }
            latest = userService.getUserById(woody.getId());
            if (!oldEmail.equals(latest.getEmail())) {
                userService.updateEmail(woody.getId(), oldEmail);
            }
            latest = userService.getUserById(woody.getId());
            if (!oldPassword.equals(latest.getPassword())) {
                userService.updatePassword(woody.getId(), latest.getPassword(), oldPassword);
            }
        }
    }

    private static void printChannel(Channel channel, MessageService messageService, UserService userService) {
        System.out.println("참여자:");
        for (UUID id : channel.getParticipantIds()) {
            System.out.println("- " + userService.getUserById(id).getUsername());
        }
        System.out.println("메시지:");
        for (Message message : messageService.getMessagesByChannelId(channel.getId())) {
            System.out.println(userService.getUserById(message.getSenderId()).getUsername()
                    + ": " + message.getContent());
        }
    }

    private static boolean existsUser(UserService service, UUID id) {
        return service.getAllUsers().stream().anyMatch(user -> user.getId().equals(id));
    }

    private static boolean existsChannel(ChannelService service, UUID id) {
        return service.getAllChannels().stream().anyMatch(channel -> channel.getId().equals(id));
    }

    private static void checkException(String name, String expected, Runnable action) {
        try {
            action.run();
            throw new IllegalStateException(name + " 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage() == null || !e.getMessage().contains(expected)) throw e;
            System.out.println("중복 이메일 검증 성공: " + e.getMessage());
        }
    }

    private static void ensure(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
