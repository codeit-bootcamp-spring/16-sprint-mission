package discodeit;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.repository.file.FileChannelRepository;
import discodeit.repository.file.FileUserRepository;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.service.file.FileChannelService;
import discodeit.service.file.FileMessageService;
import discodeit.service.file.FileUserService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileJavaApplication {

    private static final List<String> DATA_FILES = List.of("users.ser", "channels.ser", "messages.ser");

    private static int passed = 0;
    private static int failed = 0;

    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;

    // 시나리오 간에 공유하는 데이터
    private static User user1, user2, user3, user4;
    private static Channel ch1, ch2, ch3;

    public static void main(String[] args) {
        // 이전 실행에서 남은 파일이 결과에 영향을 주지 않도록 먼저 지운다
        cleanUp();

        userService = new FileUserService(new FileUserRepository());
        channelService = new FileChannelService(userService, new FileChannelRepository());
        messageService = new FileMessageService(userService, channelService);

        runScenario("1. User CRUD", FileJavaApplication::testUser);
        runScenario("2. Channel CRUD", FileJavaApplication::testChannel);
        runScenario("3. Message CRUD", FileJavaApplication::testMessage);
        runScenario("4. 파일 영속성 (서비스를 새로 만들어도 데이터 유지)", FileJavaApplication::testPersistence);
        runScenario("5. 채널 삭제 시 메세지/유저 연결 정리", FileJavaApplication::testChannelDeleteCascade);
        runScenario("6. 유저 삭제 시 메세지/채널 참여 정리", FileJavaApplication::testUserDeleteCascade);

        System.out.println();
        System.out.printf("결과: %d 통과 / %d 실패%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---------------------------------------------------------------- 시나리오

    private static void testUser() {
        user1 = userService.create("삼성");
        user2 = userService.create("애플");
        user3 = userService.create("LG");
        user4 = userService.create("화웨이");

        check("유저 4명 생성 후 전체 조회 = 4", userService.findAll().size() == 4);
        check("단건 조회", userService.find(user1.getId()).map(User::getUserName).orElse("").equals("삼성"));

        userService.update(user1.getId(), "Samsung");
        check("이름 수정 반영", userService.find(user1.getId()).map(User::getUserName).orElse("").equals("Samsung"));
        check("없는 유저 수정 시 예외", throwsNoSuchElement(() -> userService.update(UUID.randomUUID(), "x")));

        userService.delete(user4.getId());
        check("삭제 후 전체 조회 = 3", userService.findAll().size() == 3);
        check("삭제된 유저 단건 조회 = empty", userService.find(user4.getId()).isEmpty());
        check("이미 삭제된 유저 재삭제 시 예외", throwsNoSuchElement(() -> userService.delete(user4.getId())));
    }

    private static void testChannel() {
        List<User> users = List.of(user1, user2, user3);

        ch1 = channelService.create(users, "Seoul");
        ch2 = channelService.create(users, "Tokyo");
        ch3 = channelService.create(users, "Hongkong");

        check("채널 3개 생성 후 전체 조회 = 3", channelService.findAll().size() == 3);
        check("단건 조회", channelService.find(ch1.getId()).map(Channel::getChannelName).orElse("").equals("Seoul"));
        check("채널 참여 유저 수 = 3",
                channelService.find(ch1.getId()).map(c -> c.getUserList().size()).orElse(-1) == 3);
        check("유저 쪽 채널 목록에도 채널이 추가됨",
                userService.find(user1.getId()).map(u -> u.getChannelList().size()).orElse(-1) == 3);

        channelService.update(ch1.getId(), "Busan");
        check("채널명 수정 반영", channelService.find(ch1.getId()).map(Channel::getChannelName).orElse("").equals("Busan"));
        check("없는 채널 수정 시 예외", throwsNoSuchElement(() -> channelService.update(UUID.randomUUID(), "x")));
        check("삭제된 유저로 채널 생성 시 예외",
                throwsNoSuchElement(() -> channelService.create(List.of(user4), "Ghost")));
    }

    private static void testMessage() {
        Message m1 = messageService.create(user1, ch1, "첫번째 테스트 코멘트 입니다.");
        messageService.create(user2, ch2, "I want to buy Samsung Galaxy Fold 8");
        messageService.create(user2, ch2, "I want to buy IPhone Duo too");
        messageService.create(user3, ch3, "아 발로란트 하고싶다.");

        check("메세지 4개 생성 후 전체 조회 = 4", messageService.findAll().size() == 4);
        check("단건 조회", messageService.find(m1.getId()).map(Message::getComment)
                .orElse("").equals("첫번째 테스트 코멘트 입니다."));

        messageService.update(m1.getId(), "수정된 코멘트");
        check("메세지 수정 반영", messageService.find(m1.getId()).map(Message::getComment)
                .orElse("").equals("수정된 코멘트"));
        check("없는 메세지 수정 시 예외", throwsNoSuchElement(() -> messageService.update(UUID.randomUUID(), "x")));

        messageService.delete(m1.getId());
        check("삭제 후 전체 조회 = 3", messageService.findAll().size() == 3);
        check("이미 삭제된 메세지 재삭제 시 예외", throwsNoSuchElement(() -> messageService.delete(m1.getId())));

        check("삭제된 유저로 메세지 생성 시 예외",
                throwsNoSuchElement(() -> messageService.create(user4, ch1, "유령 유저")));
        check("존재하지 않는 채널로 메세지 생성 시 예외",
                throwsNoSuchElement(() -> messageService.create(user1, new Channel("없는 채널"), "유령 채널")));
    }

    private static void testPersistence() {
        // 같은 파일을 읽는 서비스 묶음을 새로 만든다 (= 프로그램을 껐다 켠 상황)
        UserService newUserService = new FileUserService(new FileUserRepository());
        ChannelService newChannelService = new FileChannelService(newUserService, new FileChannelRepository());
        MessageService newMessageService = new FileMessageService(newUserService, newChannelService);

        check("유저 3명 유지", newUserService.findAll().size() == 3);
        check("채널 3개 유지", newChannelService.findAll().size() == 3);
        check("메세지 3개 유지", newMessageService.findAll().size() == 3);
        check("수정된 유저명 유지",
                newUserService.find(user1.getId()).map(User::getUserName).orElse("").equals("Samsung"));
        check("수정된 채널명 유지",
                newChannelService.find(ch1.getId()).map(Channel::getChannelName).orElse("").equals("Busan"));
    }

    private static void testChannelDeleteCascade() {
        User a = userService.create("채널삭제_A");
        User b = userService.create("채널삭제_B");
        Channel channel = channelService.create(List.of(a, b), "삭제될 채널");
        messageService.create(a, channel, "삭제될 메세지 1");
        messageService.create(b, channel, "삭제될 메세지 2");

        UUID channelId = channel.getId();
        check("삭제 전 채널의 메세지 = 2", countMessagesInChannel(channelId) == 2);

        channelService.delete(channelId);

        check("채널 단건 조회 = empty", channelService.find(channelId).isEmpty());
        check("채널에 달린 메세지도 삭제됨", countMessagesInChannel(channelId) == 0);
        check("유저의 채널 목록에서도 제거됨",
                userService.find(a.getId()).map(u -> !hasChannel(u, channelId)).orElse(false));
        check("없는 채널 삭제 시 예외", throwsNoSuchElement(() -> channelService.delete(channelId)));
    }

    private static void testUserDeleteCascade() {
        User x = userService.create("유저삭제_X");
        User y = userService.create("유저삭제_Y");
        Channel channel = channelService.create(List.of(x, y), "공용 채널");
        messageService.create(x, channel, "X가 쓴 메세지 1");
        messageService.create(x, channel, "X가 쓴 메세지 2");
        messageService.create(y, channel, "Y가 쓴 메세지");

        check("삭제 전 X의 메세지 = 2", countMessagesOfUser(x.getId()) == 2);

        userService.delete(x.getId());

        check("유저 단건 조회 = empty", userService.find(x.getId()).isEmpty());
        check("X가 쓴 메세지 모두 삭제됨", countMessagesOfUser(x.getId()) == 0);
        check("Y가 쓴 메세지는 유지됨", countMessagesOfUser(y.getId()) == 1);
        check("채널 참여 유저에서 X 제거됨",
                channelService.find(channel.getId())
                        .map(c -> c.getUserList().stream().noneMatch(u -> u.getId().equals(x.getId())))
                        .orElse(false));
    }

    // ---------------------------------------------------------------- 헬퍼

    private static long countMessagesInChannel(UUID channelId) {
        return messageService.findAll().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .count();
    }

    private static long countMessagesOfUser(UUID userId) {
        return messageService.findAll().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .count();
    }

    private static boolean hasChannel(User user, UUID channelId) {
        return user.getChannelList().stream().anyMatch(c -> c.getId().equals(channelId));
    }

    private static boolean throwsNoSuchElement(Runnable action) {
        try {
            action.run();
            return false;
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            failed++;
            System.out.println("  [FAIL] " + name);
        }
    }

    // 시나리오 하나가 예상치 못한 예외로 중단돼도 다음 시나리오는 계속 실행한다
    private static void runScenario(String title, Runnable scenario) {
        System.out.println();
        System.out.println("=== " + title);
        try {
            scenario.run();
        } catch (Exception e) {
            failed++;
            System.out.println("  [FAIL] 시나리오 중단: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void cleanUp() {
        for (String file : DATA_FILES) {
            try {
                Files.deleteIfExists(Path.of(file));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}