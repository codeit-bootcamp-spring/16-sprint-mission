package discodeit;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.service.file.FileChannelService;
import discodeit.service.file.FileMessageService;
import discodeit.service.file.FileUserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileJavaApplication {

    // ===== 채널 삭제 오케스트레이터 (기존 코드 그대로) =====
    public static class ChannelDeletionService {
        private final UserService userService;
        private final ChannelService channelService;
        private final MessageService messageService;

        public ChannelDeletionService(
                UserService userService,
                ChannelService channelService,
                MessageService messageService) {
            this.userService = userService;
            this.channelService = channelService;
            this.messageService = messageService;
        }

        public void delete(UUID channelId) {
            channelService.find(channelId)
                    .orElseThrow(() ->
                            new NoSuchElementException("삭제할 채널이 없습니다."));

            messageService.deleteMessagesByChannelId(channelId);
            userService.removeChannelFromUsers(channelId);
            channelService.delete(channelId);
        }
    }

    // ===== 테스트 보조 =====
    private static int passed = 0;
    private static int failed = 0;

    private static void section(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static void expectNoSuchElement(String name, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("[FAIL] " + name + " (예외가 발생하지 않음)");
        } catch (NoSuchElementException e) {
            passed++;
            System.out.println("[PASS] " + name);
        } catch (RuntimeException e) {
            failed++;
            System.out.println("[FAIL] " + name + " (다른 예외: " + e.getClass().getSimpleName() + ")");
        }
    }

    // 파일을 새로 읽어 오는 "다른 서비스 객체 묶음" (리스너 중복 등록을 피하려고 별도로 생성)
    private static UserService freshUserService() {
        return new FileUserService();
    }

    private static ChannelService freshChannelService() {
        return new FileChannelService(freshUserService());
    }

    private static MessageService freshMessageService() {
        UserService us = freshUserService();
        return new FileMessageService(us, new FileChannelService(us));
    }

    private static void cleanFiles() throws IOException {
        for (String file : List.of("users.ser", "channels.ser", "messages.ser")) {
            Files.deleteIfExists(Path.of(file));
        }
    }

    private static boolean userHasChannel(User user, UUID channelId) {
        return user.getChannelList().stream().anyMatch(c -> c.getId().equals(channelId));
    }

    public static void main(String[] args) throws IOException {
        // 이전 실행의 .ser 파일이 남아 있으면 결과가 누적되므로 시작 전에 삭제
        cleanFiles();

        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService(userService);
        MessageService messageService = new FileMessageService(userService, channelService);
        ChannelDeletionService deletionService =
                new ChannelDeletionService(userService, channelService, messageService);

        // ------------------------------------------------------------
        section("1. User CRUD");
        User u1 = userService.create("Samsung");
        User u2 = userService.create("Apple");
        User u3 = userService.create("LG");
        User u4 = userService.create("Huawei");

        check("유저 4명 생성 후 findAll 크기 = 4", userService.findAll().size() == 4);
        check("단건 조회 성공", userService.find(u1.getId()).isPresent());

        userService.update(u1.getId(), "Samsung Electronics");
        check("유저 이름 수정 반영",
                userService.find(u1.getId()).get().getUserName().equals("Samsung Electronics"));
        check("새 서비스 객체로 다시 읽어도 수정 유지(파일 저장 확인)",
                freshUserService().find(u1.getId()).get().getUserName().equals("Samsung Electronics"));
        expectNoSuchElement("없는 유저 수정 시 예외",
                () -> userService.update(UUID.randomUUID(), "x"));

        userService.delete(u4.getId());
        check("유저 삭제 후 findAll 크기 = 3", userService.findAll().size() == 3);
        check("삭제된 유저 단건 조회 시 empty", userService.find(u4.getId()).isEmpty());
        expectNoSuchElement("없는 유저 삭제 시 예외",
                () -> userService.delete(UUID.randomUUID()));

        // ------------------------------------------------------------
        section("2. Channel CRUD");
        Channel seoul = channelService.create(List.of(u1, u2, u3), "Seoul");
        Channel tokyo = channelService.create(List.of(u1, u2), "Tokyo");
        Channel busan = channelService.create(List.of(u3), "Busan");

        check("채널 3개 생성 후 findAll 크기 = 3", channelService.findAll().size() == 3);
        check("Seoul 멤버 수 = 3",
                channelService.find(seoul.getId()).get().getUserList().size() == 3);

        channelService.update(busan.getId(), "Daegu");
        check("채널 이름 수정 반영",
                channelService.find(busan.getId()).get().getChannelName().equals("Daegu"));
        check("새 서비스 객체로 다시 읽어도 수정 유지",
                freshChannelService().find(busan.getId()).get().getChannelName().equals("Daegu"));

        expectNoSuchElement("존재하지 않는 유저로 채널 생성 시 예외",
                () -> channelService.create(List.of(new User("ghost")), "Ghost"));
        expectNoSuchElement("없는 채널 수정 시 예외",
                () -> channelService.update(UUID.randomUUID(), "x"));

        // 채널 생성 시 users.ser 쪽 User.channelList에도 채널이 반영돼야 하는지 확인
        // (현재 구현에서는 FAIL이 예상됨: FileChannelService.create가 users.ser를 갱신하지 않음)
        User u1Saved = userService.find(u1.getId()).get();
        check("채널 생성 후 users.ser의 u1.channelList에 Seoul, Tokyo 반영 (현재 FAIL 예상)",
                userHasChannel(u1Saved, seoul.getId()) && userHasChannel(u1Saved, tokyo.getId()));

        // ------------------------------------------------------------
        section("3. Message CRUD");
        Message m1 = messageService.create(u1, seoul, "hello seoul");
        Message m2 = messageService.create(u2, seoul, "hi from apple");
        Message m3 = messageService.create(u1, tokyo, "tokyo message");
        Message m4 = messageService.create(u3, busan, "daegu message");

        check("메시지 4개 생성 후 findAll 크기 = 4", messageService.findAll().size() == 4);
        check("단건 조회 내용 일치",
                messageService.find(m1.getId()).get().getComment().equals("hello seoul"));

        messageService.update(m1.getId(), "hello seoul (edited)");
        check("메시지 수정 반영",
                messageService.find(m1.getId()).get().getComment().equals("hello seoul (edited)"));
        check("새 서비스 객체로 다시 읽어도 수정 유지(update의 save 확인)",
                freshMessageService().find(m1.getId()).get().getComment().equals("hello seoul (edited)"));

        expectNoSuchElement("존재하지 않는 유저로 메시지 생성 시 예외",
                () -> messageService.create(new User("ghost"), seoul, "x"));
        expectNoSuchElement("존재하지 않는 채널로 메시지 생성 시 예외",
                () -> messageService.create(u1, new Channel("ghost"), "x"));
        expectNoSuchElement("없는 메시지 수정 시 예외",
                () -> messageService.update(UUID.randomUUID(), "x"));

        messageService.delete(m4.getId());
        check("메시지 삭제 후 findAll 크기 = 3", messageService.findAll().size() == 3);
        check("삭제된 메시지 조회 시 empty", messageService.find(m4.getId()).isEmpty());
        expectNoSuchElement("없는 메시지 삭제 시 예외",
                () -> messageService.delete(m4.getId()));

        // ------------------------------------------------------------
        section("4. 유저 삭제 시 연쇄 삭제 (u2 = Apple)");
        userService.delete(u2.getId());

        check("u2 삭제됨", userService.find(u2.getId()).isEmpty());
        check("u2가 작성한 메시지(m2) 삭제됨", messageService.find(m2.getId()).isEmpty());
        check("다른 유저의 메시지(m1, m3)는 유지, 총 2개", messageService.findAll().size() == 2);
        check("Seoul 멤버에서 u2 제거됨(멤버 2명)",
                channelService.find(seoul.getId()).get().getUserList().size() == 2);
        check("Tokyo 멤버에서 u2 제거됨(멤버 1명)",
                channelService.find(tokyo.getId()).get().getUserList().size() == 1);
        check("새 서비스 객체로 다시 읽어도 u2의 메시지 없음",
                freshMessageService().findAll().stream()
                        .noneMatch(m -> m.getUser().getId().equals(u2.getId())));

        // ------------------------------------------------------------
        section("5. 채널 삭제 시 연쇄 삭제 (Tokyo, ChannelDeletionService 사용)");
        deletionService.delete(tokyo.getId());

        check("Tokyo 채널 삭제됨", channelService.find(tokyo.getId()).isEmpty());
        check("Tokyo 메시지(m3) 삭제됨", messageService.find(m3.getId()).isEmpty());
        check("다른 채널의 메시지(m1)는 유지, 총 1개", messageService.findAll().size() == 1);
        check("users.ser의 모든 유저 channelList에 Tokyo 없음",
                userService.findAll().stream().noneMatch(u -> userHasChannel(u, tokyo.getId())));
        check("channels.ser의 남은 채널 멤버들의 channelList에도 Tokyo 없음",
                channelService.findAll().stream()
                        .flatMap(c -> c.getUserList().stream())
                        .noneMatch(u -> userHasChannel(u, tokyo.getId())));
        check("새 서비스 객체로 다시 읽어도 Tokyo 없음",
                freshChannelService().find(tokyo.getId()).isEmpty());
        expectNoSuchElement("없는 채널을 ChannelDeletionService로 삭제 시 예외",
                () -> deletionService.delete(UUID.randomUUID()));

        // ------------------------------------------------------------
        section("최종 상태");
        System.out.println("유저: " + userService.findAll());
        System.out.println("채널: " + channelService.findAll());
        System.out.println("메시지: " + messageService.findAll());

        System.out.println();
        System.out.println("===== 결과: 통과 " + passed + " / 실패 " + failed + " =====");
    }
}
