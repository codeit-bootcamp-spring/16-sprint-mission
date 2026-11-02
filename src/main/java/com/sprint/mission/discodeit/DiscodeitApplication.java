package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.AuthService;
import com.sprint.mission.discodeit.service.basic.BinaryContentService;
import com.sprint.mission.discodeit.service.basic.ReadStatusService;
import com.sprint.mission.discodeit.service.basic.UserStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 스프링 컨텍스트로 서비스를 주입받아 테스트한다. (저장소: File 구현체 - @Repository가 붙은 것)
 * 실행 전에 file-data-map 폴더를 지워 두면 이전 형식의 .ser 파일 때문에 생기는 오류를 피할 수 있다.
 */
@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        userService = context.getBean(UserService.class);
        channelService = context.getBean(ChannelService.class);
        messageService = context.getBean(MessageService.class);
        readStatusService = context.getBean(ReadStatusService.class);
        userStatusService = context.getBean(UserStatusService.class);
        binaryContentService = context.getBean(BinaryContentService.class);
        authService = context.getBean(AuthService.class);

        runAllTests();
        context.close();   // 테스트가 끝나면 웹 서버까지 종료한다. 계속 띄워 두려면 이 줄을 지운다.
    }

    // ====================== 테스트 대상 서비스 ======================
    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;
    private static ReadStatusService readStatusService;
    private static UserStatusService userStatusService;
    private static BinaryContentService binaryContentService;
    private static AuthService authService;

    // ====================== 시나리오 공유 상태 ======================
    private static int passed;
    private static int failed;
    private static String suffix;
    private static String woodyName;
    private static String woodyEmail;
    private static String woodyNewName;
    private static UserResponseDto woody;
    private static UserResponseDto alice;
    private static UserResponseDto bob;
    private static UserStatusResponseDto woodyStatus;
    private static ChannelResponseDto publicCh;
    private static ChannelResponseDto privateCh;
    private static MessageResponseDto m1;
    private static MessageResponseDto m2;
    private static ReadStatusResponseDto rs;

    private static void runAllTests() {
        passed = 0;
        failed = 0;
        // 파일 저장소에 이전 실행 데이터가 남아 있어도 중복 예외가 나지 않도록 매번 다른 접미사를 쓴다.
        suffix = UUID.randomUUID().toString().substring(0, 8);
        try {
            userTest();
            userStatusTest();
            authTest();
            channelTest();
            messageTest();
            readStatusTest();
            deleteTest();
        } catch (Exception e) {
            failed++;
            System.out.println("\n[ERROR] 예상하지 못한 예외로 시나리오가 중단되었습니다: " + e);
            e.printStackTrace();
        }
        System.out.println("\n==============================");
        System.out.println("결과: PASS " + passed + " / FAIL " + failed);
        System.out.println("==============================");
    }

    // ------------------------------------------------------------------
    // 1. User
    // ------------------------------------------------------------------
    private static void userTest() {
        section("1. User");
        woodyName = "woody-" + suffix;
        woodyEmail = "woody-" + suffix + "@codeit.com";
        woody = userService.create(new UserRequestDto(woodyName, woodyEmail, "pw1234", attachment("woody-profile.png")));
        alice = userService.create(new UserRequestDto("alice-" + suffix, "alice-" + suffix + "@codeit.com", "pw1234", null));
        bob = userService.create(new UserRequestDto("bob-" + suffix, "bob-" + suffix + "@codeit.com", "pw1234", null));

        // 주의: UserResponseDto 필드명이 profiledId(오타)다. 필드명을 고치면 아래 호출도 같이 바꿀 것.
        check("프로필 있는 유저 생성 -> profileId 존재", woody.profiledId() != null);
        check("프로필 없는 유저 생성 -> profileId null", alice.profiledId() == null);
        check("생성 직후 온라인 상태", woody.online());
        expectThrows("중복 username 생성 시 예외", RuntimeException.class, () ->
                userService.create(new UserRequestDto(woodyName, "other-" + suffix + "@codeit.com", "pw", null)));
        expectThrows("중복 email 생성 시 예외", RuntimeException.class, () ->
                userService.create(new UserRequestDto("other-" + suffix, woodyEmail, "pw", null)));
        check("find", userService.find(woody.id()).username().equals(woodyName));
        check("findAll에 생성한 유저 포함", userService.findAll().stream().anyMatch(u -> u.id().equals(woody.id())));
        check("findByUsername", userService.findByUsername(woodyName).id().equals(woody.id()));
        check("findByEmail", userService.findByEmail(woodyEmail).id().equals(woody.id()));
        expectThrows("없는 유저 find 시 예외", NoSuchElementException.class, () -> userService.find(UUID.randomUUID()));

        UUID oldProfileId = woody.profiledId();
        woodyNewName = "woody2-" + suffix;
        UserResponseDto updated = userService.update(woody.id(),
                new UserRequestDto(woodyNewName, null, null, attachment("woody-new-profile.png")));
        check("수정: username 변경", updated.username().equals(woodyNewName));
        check("수정: email 유지", updated.email().equals(woodyEmail));
        check("수정: 프로필 교체(새 id)", updated.profiledId() != null && !updated.profiledId().equals(oldProfileId));
        expectThrows("수정: 기존 프로필 삭제됨", NoSuchElementException.class, () -> binaryContentService.find(oldProfileId));
        UserResponseDto keepProfile = userService.update(woody.id(), new UserRequestDto(null, null, null, null));
        check("수정: 프로필 미지정 시 기존 프로필 유지", updated.profiledId().equals(keepProfile.profiledId()));
    }

    // ------------------------------------------------------------------
    // 2. UserStatus
    // ------------------------------------------------------------------
    private static void userStatusTest() {
        section("2. UserStatus");
        woodyStatus = userStatusService.updateByUserId(woody.id());
        check("updateByUserId(woody) -> woody의 상태", woodyStatus.userId().equals(woody.id()));
        UserStatusResponseDto aliceStatus = userStatusService.updateByUserId(alice.id());
        // 파일 저장소의 findByUserId가 userId를 무시하면 여기서 FAIL 난다.
        check("updateByUserId(alice) -> alice의 상태(다른 유저 상태가 아님)", aliceStatus.userId().equals(alice.id()));
        check("find", userStatusService.find(woodyStatus.userStatusId()).userId().equals(woody.id()));
        expectThrows("같은 유저의 UserStatus 중복 생성 시 예외", IllegalArgumentException.class, () ->
                userStatusService.create(new UserStatusRequestDto(woody.id())));
        expectThrows("없는 유저의 UserStatus 생성 시 예외", NoSuchElementException.class, () ->
                userStatusService.create(new UserStatusRequestDto(UUID.randomUUID())));

        // 아래 두 항목은 UserStatusService 수정(update -> UserStatusUpdateDto, findAll() 파라미터 제거) 반영 기준이다.
        Instant newTime = Instant.now();
        UserStatusResponseDto statusUpdated = userStatusService.update(
                woodyStatus.userStatusId(), new UserStatusUpdateDto(newTime));
        check("update: 같은 상태 + 주인(userId) 불변",
                statusUpdated.userStatusId().equals(woodyStatus.userStatusId())
                        && statusUpdated.userId().equals(woody.id()));
        check("update: 접속 시각 반영", newTime.equals(statusUpdated.updatedAt()));
        List<UUID> statusUserIds = userStatusService.findAll().stream().map(UserStatusResponseDto::userId).toList();
        check("findAll: 모든 상태 조회(woody, alice 포함)",
                statusUserIds.contains(woody.id()) && statusUserIds.contains(alice.id()));
    }

    // ------------------------------------------------------------------
    // 3. Auth
    // ------------------------------------------------------------------
    private static void authTest() {
        section("3. Auth");
        UserResponseDto loggedIn = authService.login(new LoginRequestDto(woodyNewName, "pw1234"));
        check("로그인 성공 -> 유저 정보 반환", loggedIn.id().equals(woody.id()));
        expectThrows("잘못된 비밀번호 -> 예외", NoSuchElementException.class, () ->
                authService.login(new LoginRequestDto(woodyNewName, "wrong")));
        expectThrows("없는 유저 -> 예외", NoSuchElementException.class, () ->
                authService.login(new LoginRequestDto("nobody-" + suffix, "pw1234")));
    }

    // ------------------------------------------------------------------
    // 4. Channel
    // ------------------------------------------------------------------
    private static void channelTest() {
        section("4. Channel");
        publicCh = channelService.createPublicChannel(
                new PublicChannelRequestDto("general-" + suffix, ChannelType.PUBLIC, "공개 채널"));
        privateCh = channelService.createPrivateChannel(
                new PrivateChannelRequestDto(List.of(woody.id(), alice.id()), ChannelType.PRIVATE));

        check("PUBLIC: 이름/설명 저장", ("general-" + suffix).equals(publicCh.channelName())
                && "공개 채널".equals(publicCh.description()));
        check("PUBLIC: 타입", publicCh.channelType() == ChannelType.PUBLIC);
        check("PUBLIC: 참여자 목록 null", publicCh.userIds() == null);
        check("PUBLIC: 메시지 없으면 lastMessageAt null", publicCh.lastMessageAt() == null);
        check("PRIVATE: 이름/설명 null", privateCh.channelName() == null && privateCh.description() == null);
        check("PRIVATE: 타입", privateCh.channelType() == ChannelType.PRIVATE);
        check("PRIVATE: 참여자 2명", privateCh.userIds() != null && privateCh.userIds().size() == 2
                && privateCh.userIds().containsAll(List.of(woody.id(), alice.id())));
        check("find(PRIVATE): 참여자 id 포함",
                channelService.find(privateCh.channelId()).userIds().containsAll(List.of(woody.id(), alice.id())));
        expectThrows("없는 채널 find 시 예외", NoSuchElementException.class, () ->
                channelService.find(UUID.randomUUID()));

        List<ChannelResponseDto> woodyChannels = channelService.findAllByUserId(woody.id());
        check("findAllByUserId(참여자): PUBLIC 포함", containsChannel(woodyChannels, publicCh.channelId()));
        check("findAllByUserId(참여자): 참여한 PRIVATE 포함", containsChannel(woodyChannels, privateCh.channelId()));
        List<ChannelResponseDto> bobChannels = channelService.findAllByUserId(bob.id());
        check("findAllByUserId(비참여자): PUBLIC 포함", containsChannel(bobChannels, publicCh.channelId()));
        check("findAllByUserId(비참여자): 남의 PRIVATE 제외", !containsChannel(bobChannels, privateCh.channelId()));

        ChannelResponseDto renamed = channelService.update(publicCh.channelId(),
                new PublicChannelUpdateDto("general2-" + suffix, null));
        check("PUBLIC 수정: 이름 변경", ("general2-" + suffix).equals(renamed.channelName()));
        check("PUBLIC 수정: 설명 유지", "공개 채널".equals(renamed.description()));
        expectThrows("PRIVATE 수정 시 예외", UnsupportedOperationException.class, () ->
                channelService.update(privateCh.channelId(), new PublicChannelUpdateDto("x", "y")));
    }

    // ------------------------------------------------------------------
    // 5. Message
    // ------------------------------------------------------------------
    private static void messageTest() {
        section("5. Message");
        m1 = messageService.create(new MessageRequestDto(
                publicCh.channelId(), "안녕하세요", woody.id(),
                List.of(attachment("a.txt"), attachment("b.txt"))));
        m2 = messageService.create(new MessageRequestDto(
                publicCh.channelId(), "첨부 없는 메시지", alice.id(), null));

        check("첨부 2개 -> attachmentIds 2개", m1.attachmentIds() != null && m1.attachmentIds().size() == 2);
        check("첨부 없음 -> attachmentIds 비어 있음", m2.attachmentIds() != null && m2.attachmentIds().isEmpty());
        check("첨부파일이 실제로 저장됨(findAllByIdIn)", binaryContentService.findAllByIdIn(m1.attachmentIds()).size() == 2);
        check("find", messageService.find(m1.messageId()).content().equals("안녕하세요"));
        check("findAllByChannelId: 2개", messageService.findAllByChannelId(publicCh.channelId()).size() == 2);
        check("채널 조회에 최근 메시지 시간 포함", channelService.find(publicCh.channelId()).lastMessageAt() != null);

        MessageResponseDto edited = messageService.update(m2.messageId(),
                new MessageUpdateDto("수정된 내용"));
        check("메시지 수정: 내용 변경", edited.content().equals("수정된 내용"));
        expectThrows("없는 채널에 메시지 생성 시 예외", NoSuchElementException.class, () ->
                messageService.create(new MessageRequestDto(UUID.randomUUID(), "x", woody.id(), null)));
        expectThrows("없는 작성자로 메시지 생성 시 예외", NoSuchElementException.class, () ->
                messageService.create(new MessageRequestDto(publicCh.channelId(), "x", UUID.randomUUID(), null)));
    }

    // ------------------------------------------------------------------
    // 6. ReadStatus
    // ------------------------------------------------------------------
    private static void readStatusTest() {
        section("6. ReadStatus");
        // PRIVATE 채널 생성 시 woody/alice의 ReadStatus가 이미 만들어졌으므로 PUBLIC 채널로 생성한다.
        rs = readStatusService.create(new ReadStatusRequestDto(publicCh.channelId(), woody.id()));
        check("생성: 채널/유저 id 일치", rs.channelId().equals(publicCh.channelId()) && rs.userId().equals(woody.id()));
        expectThrows("같은 (유저, 채널) 중복 생성 시 예외", IllegalArgumentException.class, () ->
                readStatusService.create(new ReadStatusRequestDto(publicCh.channelId(), woody.id())));
        expectThrows("없는 채널로 생성 시 예외", NoSuchElementException.class, () ->
                readStatusService.create(new ReadStatusRequestDto(UUID.randomUUID(), woody.id())));
        expectThrows("없는 유저로 생성 시 예외", NoSuchElementException.class, () ->
                readStatusService.create(new ReadStatusRequestDto(publicCh.channelId(), UUID.randomUUID())));
        check("find", readStatusService.find(rs.statusId()).userId().equals(woody.id()));
        List<UUID> woodyReadChannelIds = readStatusService.findAllByUserId(woody.id()).stream()
                .map(ReadStatusResponseDto::channelId).toList();
        check("findAllByUserId: PUBLIC + PRIVATE 둘 다 포함",
                woodyReadChannelIds.contains(publicCh.channelId()) && woodyReadChannelIds.contains(privateCh.channelId()));
        // 응답 DTO에 lastReadAt이 없어 값 자체는 검증할 수 없고, 예외 없이 같은 객체가 반환되는지만 확인한다.
        ReadStatusResponseDto rsUpdated = readStatusService.update(rs.statusId(), new ReadStatusUpdateDto(Instant.now()));
        check("update: 같은 ReadStatus 반환", rsUpdated.statusId().equals(rs.statusId()));
    }

    // ------------------------------------------------------------------
    // 7. Delete (연쇄 삭제)
    // ------------------------------------------------------------------
    private static void deleteTest() {
        section("7. Delete (연쇄 삭제)");
        messageService.delete(m1.messageId());
        expectThrows("메시지 삭제 -> 조회 불가", NoSuchElementException.class, () -> messageService.find(m1.messageId()));
        expectThrows("메시지 삭제 -> 첨부파일도 삭제", NoSuchElementException.class, () ->
                binaryContentService.find(m1.attachmentIds().get(0)));

        MessageResponseDto m3 = messageService.create(new MessageRequestDto(
                privateCh.channelId(), "비공개 메시지", woody.id(), List.of(attachment("c.txt"))));
        channelService.delete(privateCh.channelId());
        expectThrows("채널 삭제 -> 채널 조회 불가", NoSuchElementException.class, () ->
                channelService.find(privateCh.channelId()));
        check("채널 삭제 -> ReadStatus 삭제", readStatusService.findAllByUserId(woody.id()).stream()
                .noneMatch(r -> r.channelId().equals(privateCh.channelId())));
        expectThrows("채널 삭제 -> 메시지 삭제", NoSuchElementException.class, () -> messageService.find(m3.messageId()));
        expectThrows("채널 삭제 -> 첨부파일 삭제", NoSuchElementException.class, () ->
                binaryContentService.find(m3.attachmentIds().get(0)));

        readStatusService.delete(rs.statusId());
        expectThrows("ReadStatus 삭제 -> 조회 불가", NoSuchElementException.class, () -> readStatusService.find(rs.statusId()));

        UUID woodyProfileId = userService.find(woody.id()).profiledId();
        userService.delete(woody.id());
        expectThrows("유저 삭제 -> 유저 조회 불가", NoSuchElementException.class, () -> userService.find(woody.id()));
        expectThrows("유저 삭제 -> 프로필 삭제", NoSuchElementException.class, () -> binaryContentService.find(woodyProfileId));
        expectThrows("유저 삭제 -> UserStatus 삭제", NoSuchElementException.class, () ->
                userStatusService.find(woodyStatus.userStatusId()));

        // 정리: 남은 테스트 데이터 삭제 (파일 저장소에 계속 쌓이지 않게)
        channelService.delete(publicCh.channelId());
        userService.delete(alice.id());
        userService.delete(bob.id());
    }

    // ------------------------------------------------------------------
    // 헬퍼
    // ------------------------------------------------------------------
    private static BinaryContentRequestDto attachment(String fileName) {
        return new BinaryContentRequestDto(fileName, "text/plain", fileName.getBytes(StandardCharsets.UTF_8));
    }

    private static boolean containsChannel(List<ChannelResponseDto> channels, UUID channelId) {
        return channels.stream().anyMatch(c -> c.channelId().equals(channelId));
    }

    private static void section(String title) {
        System.out.println("\n=== " + title + " ===");
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

    private static void expectThrows(String name, Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            if (expected.isInstance(t)) {
                passed++;
                System.out.println("  [PASS] " + name);
            } else {
                failed++;
                System.out.println("  [FAIL] " + name + " (기대: " + expected.getSimpleName()
                        + ", 실제: " + t.getClass().getSimpleName() + ")");
            }
            return;
        }
        failed++;
        System.out.println("  [FAIL] " + name + " (예외가 발생하지 않음)");
    }
}