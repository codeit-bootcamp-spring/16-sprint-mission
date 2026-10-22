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
import com.sprint.mission.discodeit.test.ChannelTestRunner;
import com.sprint.mission.discodeit.test.MessageTestRunner;
import com.sprint.mission.discodeit.test.UserTestRunner;

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
        verifyRelationships(userService, channelService, messageService, "테스트 전");

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

        UserTestRunner.run(userService, channelService, messageService, woody);
        ChannelTestRunner.run(channelService, messageService, userService, woody, buzz, general);
        MessageTestRunner.run(messageService, channelService, userService, woody, buzz, general, woodyMessage);
        verifyRelationships(userService, channelService, messageService, "테스트 후");

        System.out.println("\n[저장된 데이터]");
        System.out.println("사용자: " + userService.getAllUsers().size() + "명");
        System.out.println("채널: " + channelService.getAllChannels().size() + "개");
        System.out.println("메시지: " + messageService.getAllMessages().size() + "개");
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
            MessageService messageService,
            String timing
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

        System.out.println("\n[" + timing + " 연관 ID 검증]");
        System.out.println("사용자·채널·메시지의 관계 ID가 모두 일치합니다.");
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
