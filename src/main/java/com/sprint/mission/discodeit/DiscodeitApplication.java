package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        userUpdateTest(userService);
        messageUpdateTest(userService, channelService, messageService);
        userDeleteTest(userService, channelService, messageService);
        channelUpdateTest(userService, channelService, messageService);
        channelDeleteTest(userService, channelService, messageService);
    }

    private static User createUser(UserService userService, String prefix) {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return userService.createUser(new User(
                prefix + "-" + unique,
                "initial-password",
                prefix + "-" + unique + "@example.com"
        ));
    }

    private static Channel createChannel(ChannelService channelService, User owner, String name) {
        return channelService.createChannel(new Channel(name, owner.getId()));
    }

    private static void printResult(String label, boolean passed) {
        System.out.println((passed ? "[PASS] " : "[FAIL] ") + label);
    }

    private static void printUser(String label, User user) {
        System.out.printf("%s: %s (이메일: %s, ID: %s)%n",
                label, user.getUsername(), user.getEmail(), user.getId());
    }

    private static void printChannel(ChannelService channelService, UUID channelId) {
        Channel channel = channelService.getChannelById(channelId);
        User owner = channelService.getParticipants(channelId).stream()
                .filter(user -> user.getId().equals(channel.getCreatorId()))
                .findFirst()
                .orElse(null);
        String ownerName = owner == null ? "확인 불가" : owner.getUsername();
        System.out.printf("채널: %s (방장: %s, ID: %s)%n",
                channel.getChannelName(), ownerName, channel.getId());
        System.out.println("참여자:");
        channelService.getParticipants(channelId).forEach(user ->
                System.out.println("  - " + user.getUsername()));
    }

    private static void printChatHistory(String title, MessageService messageService,
                                         UserService userService, UUID channelId) {
        System.out.println("\n--- " + title + " ---");
        List<Message> messages = messageService.getAllMessages().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();

        if (messages.isEmpty()) {
            System.out.println("(대화 내용 없음: 채널 메시지가 삭제되었습니다.)");
            return;
        }

        for (Message message : messages) {
            String senderName;
            try {
                senderName = userService.getUserById(message.getSenderId()).getUsername();
            } catch (IllegalArgumentException e) {
                senderName = "삭제된 사용자";
            }
            String time = Instant.ofEpochMilli(message.getCreatedAt())
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.printf("[%s] %s: %s%n", time, senderName, message.getContent());
        }
    }

    private static void userUpdateTest(UserService userService) {
        System.out.println("\n=== 사용자 수정 ===");
        User user = createUser(userService, "update-user");
        printUser("수정 전", userService.getUserById(user.getId()));

        userService.updateUsername(user.getId(), "updated-username");
        userService.updateEmail(user.getId(), "updated-" + user.getId() + "@example.com");
        userService.updatePassword(user.getId(), "initial-password", "updated-password");

        User updated = userService.getUserById(user.getId());
        printUser("수정 후 재조회", updated);
        printResult("사용자 이름 변경 확인", "updated-username".equals(updated.getUsername()));
        printResult("사용자 이메일 변경 확인", ("updated-" + user.getId() + "@example.com").equals(updated.getEmail()));
        printResult("사용자 비밀번호 변경 확인", "updated-password".equals(updated.getPassword()));
    }

    private static void messageUpdateTest(UserService userService, ChannelService channelService,
                                         MessageService messageService) {
        System.out.println("\n=== 메시지 수정 ===");
        User writer = createUser(userService, "message-writer");
        Channel channel = createChannel(channelService, writer, "message-room-" + UUID.randomUUID());
        Message message = messageService.createMessage(new Message(
                "처음 작성한 메시지입니다.", writer.getId(), channel.getId()
        ));

        printChatHistory("수정 전 대화", messageService, userService, channel.getId());
        messageService.updateMessage(message.getId(), writer.getId(), "수정 후 다시 조회된 메시지입니다.");
        Message updated = messageService.getMessageById(message.getId());
        printChatHistory("수정 후 대화", messageService, userService, channel.getId());
        printResult("메시지 수정 후 재조회", "수정 후 다시 조회된 메시지입니다.".equals(updated.getContent()));
    }

    private static void userDeleteTest(UserService userService,
                                       ChannelService channelService,
                                       MessageService messageService) {
        System.out.println("\n=== 사용자 삭제: 참여 채널과 본인 생성 채널 ===");
        User sharedChannelOwner = createUser(userService, "shared-owner");
        User deletedUser = createUser(userService, "deleted-user");
        User remainingParticipant = createUser(userService, "remaining-participant");

        Channel sharedChannel = createChannel(channelService, sharedChannelOwner,
                "shared-" + UUID.randomUUID());
        channelService.joinChannel(sharedChannel.getId(), deletedUser.getId());
        messageService.createMessage(new Message(
                "메시지를 작성한 사용자입니다.", deletedUser.getId(), sharedChannel.getId()
        ));

        Channel deletedUsersChannel = createChannel(channelService, deletedUser,
                "owned-" + UUID.randomUUID());
        channelService.joinChannel(deletedUsersChannel.getId(), remainingParticipant.getId());
        messageService.createMessage(new Message(
                "본인 생성 채널에서 작성한 메시지입니다.", deletedUser.getId(), deletedUsersChannel.getId()
        ));
        messageService.createMessage(new Message(
                "남은 참여자가 작성한 메시지입니다.", remainingParticipant.getId(), deletedUsersChannel.getId()
        ));

        UUID deletedUserId = deletedUser.getId();
        UUID sharedChannelId = sharedChannel.getId();
        UUID deletedUsersChannelId = deletedUsersChannel.getId();

        printUser("삭제 전 사용자", userService.getUserById(deletedUserId));
        System.out.println("삭제 전 참여만 한 채널:");
        printChannel(channelService, sharedChannelId);
        printChatHistory("삭제 전 참여 채널 대화", messageService, userService, sharedChannelId);
        System.out.println("삭제 전 본인 생성 채널:");
        printChannel(channelService, deletedUsersChannelId);
        printChatHistory("삭제 전 본인 생성 채널 대화", messageService, userService, deletedUsersChannelId);

        userService.deleteUser(deletedUserId, "initial-password");
        System.out.println("\n삭제 후 사용자 조회: 계정이 삭제되어 조회되지 않습니다.");

        boolean userLookupFailed;
        try {
            userService.getUserById(deletedUserId);
            userLookupFailed = false;
        } catch (IllegalArgumentException e) {
            userLookupFailed = true;
        }
        printResult("삭제한 사용자 조회 실패", userLookupFailed);

        System.out.println("삭제 후 남은 공유 채널:");
        printChannel(channelService, sharedChannelId);
        printChatHistory("삭제 후 참여 채널 대화", messageService, userService, sharedChannelId);
        boolean removedFromSharedChannel = channelService.getParticipants(sharedChannelId).stream()
                .noneMatch(participant -> participant.getId().equals(deletedUserId));
        printResult("참여만 했던 채널의 참여자 목록에서 제거", removedFromSharedChannel);

        boolean deletedUsersMessagesRemoved = messageService.getAllMessages().stream()
                .noneMatch(message -> message.getSenderId().equals(deletedUserId));
        printResult("삭제한 사용자가 작성한 메시지 제거", deletedUsersMessagesRemoved);

        boolean ownedChannelLookupFailed;
        try {
            channelService.getChannelById(deletedUsersChannelId);
            ownedChannelLookupFailed = false;
        } catch (IllegalArgumentException e) {
            ownedChannelLookupFailed = true;
        }
        printResult("삭제한 사용자가 만든 채널도 삭제", ownedChannelLookupFailed);
        printChatHistory("삭제 후 본인 생성 채널 대화", messageService, userService, deletedUsersChannelId);

        boolean ownedChannelMessagesRemoved = messageService.getAllMessages().stream()
                .noneMatch(message -> message.getChannelId().equals(deletedUsersChannelId));
        printResult("삭제한 사용자가 만든 채널의 메시지 제거", ownedChannelMessagesRemoved);

        User remainingUser = userService.getUserById(remainingParticipant.getId());
        System.out.println("삭제 후 남은 참여자의 채널 목록: " + remainingUser.getMyChannelIds());
        boolean remainingParticipantChannelReferenceRemoved = remainingUser.getMyChannelIds().stream()
                .noneMatch(deletedUsersChannelId::equals);
        printResult("남은 참여자의 채널 목록에서 삭제된 채널 제거", remainingParticipantChannelReferenceRemoved);
    }

    private static void channelUpdateTest(UserService userService, ChannelService channelService,
                                          MessageService messageService) {
        System.out.println("\n=== 채널 수정 ===");
        User owner = createUser(userService, "channel-update-owner");
        Channel channel = createChannel(channelService, owner, "before-" + UUID.randomUUID());
        String updatedName = "after-" + UUID.randomUUID();

        System.out.println("수정 전 채널:");
        printChannel(channelService, channel.getId());
        printChatHistory("채널 이름 수정 전 대화", messageService, userService, channel.getId());

        channelService.updateChannelName(channel.getId(), owner.getId(), updatedName);
        Channel updated = channelService.getChannelById(channel.getId());
        System.out.println("수정 후 재조회한 채널:");
        printChannel(channelService, updated.getId());
        printChatHistory("채널 이름 수정 후 대화", messageService, userService, updated.getId());
        printResult("방장이 변경한 채널 이름 재조회", updatedName.equals(updated.getChannelName()));
    }

    private static void channelDeleteTest(UserService userService,
                                          ChannelService channelService,
                                          MessageService messageService) {
        System.out.println("\n=== 채널 삭제: 권한 실패와 방장 삭제 ===");
        User owner = createUser(userService, "channel-delete-owner");
        User participant = createUser(userService, "channel-delete-participant");
        User unauthorizedUser = createUser(userService, "not-channel-owner");
        Channel channel = createChannel(channelService, owner, "delete-" + UUID.randomUUID());
        channelService.joinChannel(channel.getId(), participant.getId());
        messageService.createMessage(new Message(
                "삭제 대상 채널의 메시지입니다.", participant.getId(), channel.getId()
        ));

        UUID channelId = channel.getId();
        System.out.println("삭제 전 채널:");
        printChannel(channelService, channelId);
        printChatHistory("삭제 전 대화", messageService, userService, channelId);

        boolean unauthorizedDeleteRejected;
        try {
            channelService.deleteChannel(channelId, unauthorizedUser.getId());
            unauthorizedDeleteRejected = false;
        } catch (IllegalArgumentException e) {
            unauthorizedDeleteRejected = true;
        }
        printResult("권한 없는 사용자의 채널 삭제 거부", unauthorizedDeleteRejected);
        printResult("권한 없는 삭제 시도 후 채널 유지", channelService.getChannelById(channelId) != null);

        channelService.deleteChannel(channelId, owner.getId());
        System.out.println("삭제 후 채널 조회: 채널이 삭제되어 조회되지 않습니다.");

        boolean channelLookupFailed;
        try {
            channelService.getChannelById(channelId);
            channelLookupFailed = false;
        } catch (IllegalArgumentException e) {
            channelLookupFailed = true;
        }
        printResult("방장의 삭제 후 채널 조회 실패", channelLookupFailed);
        printChatHistory("삭제 후 대화", messageService, userService, channelId);

        boolean channelMessagesRemoved = messageService.getAllMessages().stream()
                .noneMatch(message -> message.getChannelId().equals(channelId));
        printResult("삭제된 채널의 메시지 제거", channelMessagesRemoved);

        User remainingParticipant = userService.getUserById(participant.getId());
        System.out.println("삭제 후 참여자의 채널 목록: " + remainingParticipant.getMyChannelIds());
        boolean participantChannelReferenceRemoved = remainingParticipant.getMyChannelIds().stream()
                .noneMatch(channelId::equals);
        printResult("참여자의 채널 목록에서 삭제된 채널 제거", participantChannelReferenceRemoved);
    }
}


