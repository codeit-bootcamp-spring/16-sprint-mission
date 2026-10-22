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

        // 기존 메시지 수정 예시
        System.out.println("\n[메시지 수정 예시]");
        messageService.updateMessage(
                woodyMessage.getId(),
                woody.getId(),
                "안녕하세요! 메시지를 수정했어요."
        );
        printChatRoom(
                general,
                messageService.getMessagesByChannelId(general.getId()),
                userService
        );

        // 메시지 삭제 예시
        System.out.println("\n[메시지 삭제 예시]");
        messageService.deleteMessage(woodyMessage.getId(), woody.getId());
        printChatRoom(
                general,
                messageService.getMessagesByChannelId(general.getId()),
                userService
        );

        System.out.println("\n[저장된 데이터]");
        System.out.println("사용자: " + userService.getAllUsers().size() + "명");
        System.out.println("채널: " + channelService.getAllChannels().size() + "개");
        System.out.println("메시지: " + messageService.getAllMessages().size() + "개");
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
                        new Channel(general.getChannelName(), woody)
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
                            new Message("참여하지 않은 채널에 보내는 메시지", outsider, general)
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
                if (!username.equals(user.getUsername())) {
                    userService.updateUsername(user.getId(), username);
                }
                return userService.getUserById(user.getId());
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
                    && creator.getId().equals(channel.getCreator().getId())) {
                return channelService.getChannelById(channel.getId());
            }
        }

        Channel channel = new Channel(channelName, creator);
        channelService.createChannel(channel);
        return channelService.getChannelById(channel.getId());
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
                    && sender.getId().equals(message.getSender().getId())) {
                return message;
            }
        }

        Message message = new Message(content, sender, channel);
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
                    .getUserById(message.getSender().getId())
                    .getUsername();
            String channelName = channelService
                    .getChannelById(message.getChannel().getId())
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
                    .getUserById(message.getSender().getId())
                    .getUsername();
            System.out.println(removeEmojis(username) + ": " + removeEmojis(message.getContent()));
        }
    }
}




