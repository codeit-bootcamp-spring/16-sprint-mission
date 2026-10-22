package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        // File Repository를 Basic 서비스에 주입
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(
                userRepository,
                channelRepository,
                messageRepository
        );
        ChannelService channelService = new BasicChannelService(
                channelRepository,
                userRepository,
                messageRepository
        );
        MessageService messageService = new BasicMessageService(
                userRepository,
                channelRepository,
                messageRepository
        );

        // 저장된 사용자를 재사용하고, 없으면 생성한다.
        User woody = findOrCreateUser(
                userService,
                "woody@example.com",
                "우디",
                "password123!"
        );
        User buzz = findOrCreateUser(
                userService,
                "buzz@example.com",
                "버즈",
                "password123!"
        );

        // 같은 이름의 채널이 있으면 재사용하고, 없으면 생성한다.
        Channel general = findOrCreateChannel(
                channelService,
                "일반",
                woody
        );
        Channel games = findOrCreateChannel(
                channelService,
                "게임 이야기",
                buzz
        );

        // 버즈가 일반 채널에 참여하도록 한다.
        if (!channelService.isParticipant(general.getId(), buzz.getId())) {
            channelService.joinChannel(general.getId(), buzz.getId());
        }

        // 테스트 메시지를 없으면 생성한다.
        Message woodyMessage = findOrCreateMessage(
                messageService,
                "안녕하세요! 오늘도 좋은 하루예요.",
                woody,
                general
        );
        findOrCreateMessage(
                messageService,
                "반가워요, 우디! 같이 이야기 나눠요.",
                buzz,
                general
        );
        findOrCreateMessage(
                messageService,
                "오늘은 어떤 게임을 할까요?",
                buzz,
                games
        );

        List<Channel> sampleChannels = setupAdditionalChatData(
                userService,
                channelService,
                messageService,
                woody,
                buzz
        );
        verifyRelationships(userService, channelService, messageService);

        printAppHeader(woody);
        printAllUsers(userService.getAllUsers());
        printAllChannels(channelService.getAllChannels());
        printAllMessages(
                messageService.getAllMessages(),
                userService,
                channelService
        );
        printChannelList(
                channelService.getChannelsByParticipantId(woody.getId())
        );
        printChatRoom(
                general,
                messageService.getMessagesByChannelId(general.getId()),
                userService
        );
        printChatRoom(
                games,
                messageService.getMessagesByChannelId(games.getId()),
                userService
        );
        for (Channel sampleChannel : sampleChannels) {
            printChatRoom(
                    sampleChannel,
                    messageService.getMessagesByChannelId(sampleChannel.getId()),
                    userService
            );
        }

        runExceptionExamples(
                userService,
                channelService,
                messageService,
                woody,
                buzz,
                general,
                games,
                woodyMessage
        );

        verifyRelationships(userService, channelService, messageService);

        runCrudSuccessExamples(
                userService,
                channelService,
                messageService,
                woody,
                buzz,
                general
        );
        verifyRelationships(userService, channelService, messageService);

        System.out.println("\n[저장된 데이터]");
        System.out.println("사용자: " + userService.getAllUsers().size() + "명");
        System.out.println("채널: " + channelService.getAllChannels().size() + "개");
        System.out.println("메시지: " + messageService.getAllMessages().size() + "개");
    }

    private static void runCrudSuccessExamples(
            UserService userService,
            ChannelService channelService,
            MessageService messageService,
            User woody,
            User buzz,
            Channel general
    ) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User originalWoody = userService.getUserById(woody.getId());
        String originalUsername = originalWoody.getUsername();
        String originalEmail = originalWoody.getEmail();
        String originalPassword = originalWoody.getPassword();
        User deletionUser = null;
        UUID testChannelId = null;

        try {
            System.out.println("\n=== 사용자 수정 테스트 ===");
            String updatedUsername = originalUsername + "-수정";
            String updatedEmail = "woody-test-" + suffix + "@example.com";
            String updatedPassword = "temporary-password-" + suffix;

            userService.updateUsername(woody.getId(), updatedUsername);
            userService.updateEmail(woody.getId(), updatedEmail);
            userService.updatePassword(woody.getId(), originalPassword, updatedPassword);

            User updatedWoody = userService.getUserById(woody.getId());
            ensure(updatedUsername.equals(updatedWoody.getUsername()), "사용자 이름 수정 확인 실패");
            ensure(updatedEmail.equals(updatedWoody.getEmail()), "사용자 이메일 수정 확인 실패");
            ensure(updatedPassword.equals(updatedWoody.getPassword()), "비밀번호 수정 확인 실패");
            System.out.println("수정 후 사용자: " + updatedWoody.getUsername());
            System.out.println("사용자 수정 및 재조회 테스트 성공");

            // 테스트가 기존 우디 계정에 남지 않도록 원래 정보를 되돌린다.
            userService.updateUsername(woody.getId(), originalUsername);
            userService.updateEmail(woody.getId(), originalEmail);
            userService.updatePassword(woody.getId(), updatedPassword, originalPassword);

            System.out.println("\n=== 채널 CRUD 테스트 ===");
            Channel testChannel = channelService.createChannel(
                    new Channel("테스트 채널 " + suffix, woody.getId())
            );
            testChannelId = testChannel.getId();
            final UUID channelId = testChannelId;
            System.out.println("채널 등록 완료");
            System.out.println("채널 조회: " + channelService.getChannelById(testChannelId).getChannelName());
            System.out.println("전체 채널 목록");
            for (Channel savedChannel : channelService.getAllChannels()) {
                System.out.println(savedChannel.getChannelName());
            }

            channelService.joinChannel(testChannelId, buzz.getId());
            checkExpectedException(
                    "중복 채널 참여 검증 성공",
                    "이미",
                    () -> channelService.joinChannel(channelId, buzz.getId())
            );
            System.out.println("채널 참여 테스트 성공");

            System.out.println("\n=== 채널 나가기 테스트 ===");
            channelService.leaveChannel(testChannelId, buzz.getId());
            Channel channelAfterLeave = channelService.getChannelById(testChannelId);
            User buzzAfterLeave = userService.getUserById(buzz.getId());
            ensure(!channelAfterLeave.getParticipantIds().contains(buzz.getId()),
                    "퇴장 후 채널 참여자 목록 확인 실패");
            ensure(!buzzAfterLeave.getMyChannelIds().contains(testChannelId),
                    "퇴장 후 사용자 채널 목록 확인 실패");
            System.out.println("채널 나가기 테스트 성공");
            checkExpectedException(
                    "미참여 채널 나가기 검증 성공",
                    "채널",
                    () -> channelService.leaveChannel(channelId, buzz.getId())
            );
            printChannelParticipants(channelAfterLeave, userService);
            printChatRoom(
                    channelAfterLeave,
                    messageService.getMessagesByChannelId(testChannelId),
                    userService
            );

            // 메시지 및 삭제 검증을 위해 버즈를 다시 참여시킨다.
            channelService.joinChannel(testChannelId, buzz.getId());

            deletionUser = userService.createUser(new User(
                    "탈퇴테스트-" + suffix,
                    "password123!",
                    "delete-user-" + suffix + "@example.com"
            ));
            final UUID deletionUserId = deletionUser.getId();
            channelService.joinChannel(testChannelId, deletionUser.getId());

            System.out.println("\n=== 중복 채널 이름 테스트 ===");
            checkExpectedException(
                    "중복 채널 이름 검증 성공",
                    "라는 채널이 이미 존재합니다.",
                    () -> channelService.createChannel(new Channel(
                            general.getChannelName(),
                            woody.getId()
                    ))
            );

            System.out.println("\n=== 메시지 CRUD 테스트 ===");
            Message deletionUserMessage = messageService.createMessage(new Message(
                    "사용자 삭제 시 함께 정리될 메시지",
                    deletionUserId,
                    testChannelId
            ));

            Message woodyMessage = messageService.createMessage(new Message(
                    "우디의 CRUD 확인 메시지 " + suffix,
                    woody.getId(),
                    testChannelId
            ));

            System.out.println("메시지 관계 테스트 성공");
            System.out.println("메시지 등록 완료");
            System.out.println("메시지 조회: " + messageService.getMessageById(woodyMessage.getId()).getContent());
            System.out.println("전체 메시지 목록");
            for (Message message : messageService.getMessagesByChannelId(testChannelId)) {
                System.out.println(message.getContent());
            }

            String updatedContent = "수정된 메시지 " + suffix;
            messageService.updateMessage(woodyMessage.getId(), woody.getId(), updatedContent);
            ensure(updatedContent.equals(
                    messageService.getMessageById(woodyMessage.getId()).getContent()
            ), "메시지 수정 확인 실패");
            System.out.println("수정 후 메시지: " + messageService.getMessageById(woodyMessage.getId()).getContent());

            messageService.deleteMessage(woodyMessage.getId(), woody.getId());
            ensure(messageService.getAllMessages().stream()
                            .noneMatch(message -> message.getId().equals(woodyMessage.getId())),
                    "메시지 삭제 확인 실패");
            ensure(!userService.getUserById(woody.getId()).getMyMessageIds()
                            .contains(woodyMessage.getId()),
                    "메시지 삭제 후 작성자 관계가 남아 있습니다.");
            ensure(!channelService.getChannelById(testChannelId).getMessageIds()
                            .contains(woodyMessage.getId()),
                    "메시지 삭제 후 채널 관계가 남아 있습니다.");
            System.out.println("메시지 삭제 및 관계 제거 테스트 성공");

            System.out.println("\n=== 채널 수정 테스트 ===");
            String updatedChannelName = "수정된 테스트 채널 " + suffix;
            channelService.updateChannelName(testChannelId, woody.getId(), updatedChannelName);
            Channel updatedChannel = channelService.getChannelById(testChannelId);
            ensure(updatedChannelName.equals(updatedChannel.getChannelName()), "채널 수정 확인 실패");
            System.out.println("수정 후 채널: " + updatedChannel.getChannelName());

            System.out.println("\n=== 사용자 삭제 후 관련 메시지·채널 확인 ===");
            System.out.println("사용자 삭제 전 채널 상태:");
            printChannelParticipants(updatedChannel, userService);
            printChatRoom(
                    updatedChannel,
                    messageService.getMessagesByChannelId(testChannelId),
                    userService
            );

            userService.deleteUser(deletionUserId, "password123!");
            Channel channelAfterUserDeletion = channelService.getChannelById(testChannelId);
            boolean userWasDeleted = userService.getAllUsers().stream()
                    .noneMatch(user -> user.getId().equals(deletionUserId));
            boolean participantWasRemoved = !channelAfterUserDeletion.getParticipantIds()
                    .contains(deletionUserId);
            boolean userMessagesWereDeleted = messageService.getAllMessages().stream()
                    .noneMatch(message -> message.getSenderId().equals(deletionUserId)
                            || message.getId().equals(deletionUserMessage.getId()));
            ensure(userWasDeleted && participantWasRemoved && userMessagesWereDeleted,
                    "사용자 삭제 후 관련 데이터가 정리되지 않았습니다.");
            System.out.println("사용자 삭제 테스트 성공");
            System.out.println("사용자 삭제 후 채널 상태:");
            printChannelParticipants(channelAfterUserDeletion, userService);
            printChatRoom(
                    channelAfterUserDeletion,
                    messageService.getMessagesByChannelId(testChannelId),
                    userService
            );

            System.out.println("\n=== 채널 삭제 후 사용자 관계 제거 테스트 ===");
            List<Message> messagesBeforeChannelDeletion =
                    messageService.getMessagesByChannelId(testChannelId);
            channelService.deleteChannel(testChannelId, woody.getId());

            boolean messagesRemain = messagesBeforeChannelDeletion.stream()
                    .anyMatch(message -> messageService.getAllMessages().stream()
                            .anyMatch(savedMessage -> savedMessage.getId().equals(message.getId())));
            boolean woodyRelationRemoved = !userService.getUserById(woody.getId())
                    .getMyChannelIds().contains(testChannelId);
            boolean buzzRelationRemoved = !userService.getUserById(buzz.getId())
                    .getMyChannelIds().contains(testChannelId);
            boolean channelWasDeleted = !hasChannel(channelService, testChannelId);

            System.out.println("저장된 사용자 채널 목록에서 제거:");
            System.out.println("- 우디: " + woodyRelationRemoved);
            System.out.println("- 버즈: " + buzzRelationRemoved);
            System.out.println("채널 저장소에서 삭제: " + channelWasDeleted);
            System.out.println("채널 메시지가 남아 있는가: " + messagesRemain);
            ensure(woodyRelationRemoved && buzzRelationRemoved && channelWasDeleted && !messagesRemain,
                    "채널 삭제 후 관계나 메시지가 남아 있습니다.");
            testChannelId = null;
            System.out.println("채널 삭제 테스트 성공");
        } finally {
            // 테스트 도중 실패해도 임시 사용자와 채널을 정리하고 우디 정보를 복구한다.
            if (deletionUser != null && hasUser(userService, deletionUser.getId())) {
                userService.deleteUser(deletionUser.getId(), "password123!");
            }
            if (testChannelId != null && hasChannel(channelService, testChannelId)) {
                channelService.deleteChannel(testChannelId, woody.getId());
            }

            User latestWoody = userService.getUserById(woody.getId());
            if (!originalUsername.equals(latestWoody.getUsername())) {
                userService.updateUsername(woody.getId(), originalUsername);
            }
            latestWoody = userService.getUserById(woody.getId());
            if (!originalEmail.equals(latestWoody.getEmail())) {
                userService.updateEmail(woody.getId(), originalEmail);
            }
            latestWoody = userService.getUserById(woody.getId());
            if (!originalPassword.equals(latestWoody.getPassword())) {
                userService.updatePassword(woody.getId(), latestWoody.getPassword(), originalPassword);
            }
        }
    }

    private static boolean hasChannel(ChannelService channelService, UUID channelId) {
        return channelService.getAllChannels().stream()
                .anyMatch(channel -> channel.getId().equals(channelId));
    }

    private static boolean hasUser(UserService userService, UUID userId) {
        return userService.getAllUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    private static void ensure(boolean condition, String errorMessage) {
        if (!condition) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private static void printChannelParticipants(Channel channel, UserService userService) {
        System.out.println("채널 참여자:");
        for (UUID participantId : channel.getParticipantIds()) {
            User participant = userService.getUserById(participantId);
            System.out.println("- " + removeEmojis(participant.getUsername()));
        }
    }

    private static List<Channel> setupAdditionalChatData(
            UserService userService,
            ChannelService channelService,
            MessageService messageService,
            User woody,
            User buzz
    ) {
        User alice = findOrCreateUser(
                userService,
                "alice@example.com",
                "앨리스",
                "password123!"
        );
        User jessie = findOrCreateUser(
                userService,
                "jessie@example.com",
                "제시",
                "password123!"
        );
        User rex = findOrCreateUser(
                userService,
                "rex@example.com",
                "렉스",
                "password123!"
        );

        Channel study = findOrCreateChannel(
                channelService,
                "샘플 스터디",
                alice
        );
        Channel movies = findOrCreateChannel(
                channelService,
                "샘플 영화",
                rex
        );

        List<User> users = List.of(woody, buzz, alice, jessie, rex);
        List<Channel> channels = List.of(study, movies);

        for (Channel channel : channels) {
            for (User user : users) {
                if (!channelService.isParticipant(channel.getId(), user.getId())) {
                    channelService.joinChannel(channel.getId(), user.getId());
                }
            }
        }

        for (Channel channel : channels) {
            for (User user : users) {
                findOrCreateMessage(
                        messageService,
                        channel.getChannelName() + "에서 인사하는 " + user.getUsername(),
                        user,
                        channel
                );
            }
        }

        return channels;
    }

    private static void verifyRelationships(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        for (Channel channel : channelService.getAllChannels()) {
            for (UUID participantId : channel.getParticipantIds()) {
                User participant = userService.getUserById(participantId);
                if (!participant.getMyChannelIds().contains(channel.getId())) {
                    throw new IllegalStateException(
                            "채널 참여 정보가 사용자 목록에 없습니다: " + channel.getChannelName()
                    );
                }
            }

            for (UUID messageId : channel.getMessageIds()) {
                Message message = messageService.getMessageById(messageId);
                if (!message.getChannelId().equals(channel.getId())) {
                    throw new IllegalStateException(
                            "채널의 메시지 정보가 일치하지 않습니다: " + channel.getChannelName()
                    );
                }
            }
        }

        for (User user : userService.getAllUsers()) {
            for (UUID channelId : user.getMyChannelIds()) {
                Channel channel = channelService.getChannelById(channelId);
                if (!channel.getParticipantIds().contains(user.getId())) {
                    throw new IllegalStateException(
                            "사용자 참여 정보가 채널 목록에 없습니다: " + user.getUsername()
                    );
                }
            }

            for (UUID messageId : user.getMyMessageIds()) {
                Message message = messageService.getMessageById(messageId);
                if (!message.getSenderId().equals(user.getId())) {
                    throw new IllegalStateException(
                            "사용자의 메시지 정보가 일치하지 않습니다: " + user.getUsername()
                    );
                }
            }
        }

        for (Message message : messageService.getAllMessages()) {
            User sender = userService.getUserById(message.getSenderId());
            Channel channel = channelService.getChannelById(message.getChannelId());

            if (!sender.getMyMessageIds().contains(message.getId())) {
                throw new IllegalStateException("메시지가 작성자 목록에 없습니다: " + message.getId());
            }
            if (!channel.getMessageIds().contains(message.getId())) {
                throw new IllegalStateException("메시지가 채널 목록에 없습니다: " + message.getId());
            }
        }

        System.out.println("\n[연관 ID 검증]");
        System.out.println("사용자·채널·메시지의 관계 ID가 모두 일치합니다.");
    }

    private static void runExceptionExamples(
            UserService userService,
            ChannelService channelService,
            MessageService messageService,
            User woody,
            User buzz,
            Channel general,
            Channel games,
            Message woodyMessage
    ) {
        System.out.println("\n[서비스 예외 처리 확인]");

        checkExpectedException("틀린 비밀번호", "비밀번호가 일치하지 않습니다.", () ->
                userService.updatePassword(
                        woody.getId(),
                        "wrong-password",
                        "new-password"
                )
        );

        checkExpectedException("이미 사용 중인 이메일", "이미 사용 중인 이메일입니다.", () ->
                userService.createUser(
                        new User(
                                "duplicateEmailUser",
                                "password123!",
                                woody.getEmail()
                        )
                )
        );

        checkExpectedException("이미 사용 중인 채널명", "라는 채널이 이미 존재합니다.", () ->
                channelService.createChannel(
                        new Channel(general.getChannelName(), woody.getId())
                )
        );

        checkExpectedException(
                "방장이 아닌 사용자의 채널명 변경",
                "방장만 채널을 수정할 수 있습니다.",
                () ->
                        channelService.updateChannelName(
                                games.getId(),
                                woody.getId(),
                                "권한 없는 변경 시도"
                        )
        );

        checkExpectedException(
                "다른 사용자가 작성한 메시지 수정",
                "본인이 작성한 메시지만 수정할 수 있습니다.",
                () ->
                        messageService.updateMessage(
                                woodyMessage.getId(),
                                buzz.getId(),
                                "권한 없는 수정 시도"
                        )
        );

        User outsider = new User(
                "outsider",
                "password123!",
                "exception-check-" + UUID.randomUUID() + "@example.com"
        );
        userService.createUser(outsider);

        try {
            checkExpectedException(
                    "채널 비참여자의 메시지 작성",
                    "채널에 참여한 유저만 메시지를 작성할 수 있습니다.",
                    () ->
                            messageService.createMessage(
                                    new Message("참여하지 않은 채널에 보내는 메시지", outsider.getId(), general.getId())
                            )
            );
        } finally {
            // 예외 테스트용으로 만든 사용자는 데이터 파일에 남기지 않는다.
            userService.deleteUser(outsider.getId(), "password123!");
        }
    }

    private static void checkExpectedException(
            String caseName,
            String expectedMessage,
            Runnable action
    ) {
        try {
            action.run();
            System.out.println("[예외 미발생] " + caseName
                    + " (예상 메시지: " + expectedMessage + ")");
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains(expectedMessage)) {
                System.out.println("[예외 확인] " + caseName + " -> " + e.getMessage());
            } else {
                System.out.println("[예상과 다른 예외] " + caseName
                        + " -> " + e.getMessage());
            }
        }
    }

    private static User findOrCreateUser(
            UserService userService,
            String email,
            String username,
            String password
    ) {
        for (User user : userService.getAllUsers()) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                if (username.equals(user.getUsername())) {
                    return user;
                }
                return userService.updateUsername(user.getId(), username);
            }
        }

        User user = new User(username, password, email);
        userService.createUser(user);
        return user;
    }

    private static Channel findOrCreateChannel(
            ChannelService channelService,
            String channelName,
            User creator
    ) {
        for (Channel channel : channelService.getAllChannels()) {
            if (channelName.equals(channel.getChannelName())
                    && creator.getId().equals(channel.getCreatorId())) {
                return channel;
            }
        }

        Channel channel = new Channel(channelName, creator.getId());
        return channelService.createChannel(channel);
    }

    private static Message findOrCreateMessage(
            MessageService messageService,
            String content,
            User sender,
            Channel channel
    ) {
        List<Message> messages = messageService.getMessagesByChannelId(channel.getId());

        for (Message message : messages) {
            if (content.equals(message.getContent())
                    && sender.getId().equals(message.getSenderId())) {
                return message;
            }
        }

        Message message = new Message(content, sender.getId(), channel.getId());
        messageService.createMessage(message);
        return message;
    }

    private static String removeEmojis(String text) {
        return text.replaceAll("[\\x{1F000}-\\x{1FAFF}\\x{2600}-\\x{27BF}]", "");
    }
    private static void printAppHeader(User currentUser) {
        System.out.println("로그인 사용자: " + currentUser.getUsername());
    }

    private static void printAllUsers(List<User> users) {
        System.out.println();
        System.out.println("전체 사용자:");
        for (User user : users) {
            System.out.println(user.getUsername() + " (" + user.getEmail() + ")");
        }
    }

    private static void printAllChannels(List<Channel> channels) {
        System.out.println();
        System.out.println("전체 채널:");
        for (Channel channel : channels) {
            System.out.println("# " + removeEmojis(channel.getChannelName()));
        }
    }

    private static void printAllMessages(
            List<Message> messages,
            UserService userService,
            ChannelService channelService
    ) {
        System.out.println();
        System.out.println("전체 메시지:");
        for (Message message : messages) {
            String username = userService
                    .getUserById(message.getSenderId())
                    .getUsername();
            String channelName = channelService
                    .getChannelById(message.getChannelId())
                    .getChannelName();
            System.out.println(
                    "# " + removeEmojis(channelName)
                            + " | " + removeEmojis(username)
                            + ": " + removeEmojis(message.getContent())
            );
        }
    }
    private static void printChannelList(List<Channel> channels) {
        System.out.println("채널 목록:");
        for (Channel channel : channels) {
            System.out.println("# " + removeEmojis(channel.getChannelName()));
        }
    }

    private static void printChatRoom(
            Channel channel,
            List<Message> messages,
            UserService userService
    ) {
        System.out.println();
        System.out.println(removeEmojis(channel.getChannelName()));

        if (messages.isEmpty()) {
            System.out.println("메시지가 없습니다.");
            return;
        }

        for (Message message : messages) {
            String username = userService
                    .getUserById(message.getSenderId())
                    .getUsername();
            System.out.println(removeEmojis(username) + ": " + removeEmojis(message.getContent()));
        }
    }
}
