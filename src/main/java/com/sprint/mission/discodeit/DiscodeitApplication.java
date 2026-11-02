package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	private static final List<String> DATA_FILES = List.of(
			"users.ser", "channels.ser", "messages.ser",
			"userStatuses.ser", "readStatuses.ser", "binaryContents.ser");

	public static void main(String[] args) {
		cleanData();
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// 서비스 초기화: new 대신 IoC Container에서 Bean 조회 (인터페이스 타입으로)
		UserService userService = context.getBean(UserService.class);
		AuthService authService = context.getBean(AuthService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		// =========================
		// 1. User 테스트
		// =========================
		System.out.println("===== 1. User 테스트 =====");

		UserDto user1 = userService.create(
				new UserCreateRequest("user1", "user1@test.com", "password123"),
				Optional.of(image("profile1.png")));
		UserDto user2 = userService.create(
				new UserCreateRequest("user2", "user2@test.com", "password456"),
				Optional.empty());
		UserDto user3 = userService.create(
				new UserCreateRequest("user3", "user3@test.com", "password789"),
				Optional.empty());

		System.out.println("생성된 사용자: " + user1.username() + ", " + user2.username() + ", " + user3.username());
		System.out.println("전체 사용자 수: " + userService.findAll().size());
		System.out.println("프로필 이미지 등록: " + (user1.profileId() != null));
		System.out.println("프로필 없는 사용자 profileId: " + user2.profileId());
		System.out.println("생성 직후 온라인 여부: " + user1.isOnline());
		System.out.println("UserStatus 함께 생성: " + userStatusService.findAll().size() + "건");

		expectFail("중복 username",
				() -> userService.create(new UserCreateRequest("user1", "other@test.com", "password123"), Optional.empty()));
		expectFail("중복 email",
				() -> userService.create(new UserCreateRequest("other", "user1@test.com", "password123"), Optional.empty()));
		expectFail("존재하지 않는 사용자 조회",
				() -> userService.findById(UUID.randomUUID()));

		// 이름만 수정 (나머지는 null → 기존 값 유지)
		UserDto renamed = userService.update(user1.id(),
				new UserUpdateRequest("updatedUser1", null, null), Optional.empty());
		System.out.println("이름만 수정: " + renamed.username() + " / 이메일 유지: " + renamed.email());

		// 프로필만 교체 (나머지 전부 null)
		UUID oldProfileId = renamed.profileId();
		UserDto reprofiled = userService.update(user1.id(),
				new UserUpdateRequest(null, null, null), Optional.of(image("profile1-new.png")));
		System.out.println("프로필만 교체: " + !reprofiled.profileId().equals(oldProfileId));
		expectFail("교체 전 프로필 파일 조회",
				() -> binaryContentService.findById(oldProfileId));

		expectFail("다른 사용자와 같은 이름으로 수정",
				() -> userService.update(user1.id(), new UserUpdateRequest("user2", null, null), Optional.empty()));

		printFile("users.ser");

		// =========================
		// 2. Auth / UserStatus 테스트
		// =========================
		System.out.println("\n===== 2. Auth / UserStatus 테스트 =====");

		// 마지막 접속을 10분 전으로 돌려 오프라인 만들기
		userStatusService.updateByUserId(user2.id(),
				new UserStatusUpdateRequest(Instant.now().minus(Duration.ofMinutes(10))));
		System.out.println("10분 전 접속 → 온라인 여부: " + userService.findById(user2.id()).isOnline());

		UserDto loginUser = authService.login(new UserLoginRequest("user2", "password456"));
		System.out.println("로그인 성공: " + loginUser.username() + " / 로그인 후 온라인 여부: " + loginUser.isOnline());

		expectFail("틀린 비밀번호로 로그인",
				() -> authService.login(new UserLoginRequest("user2", "wrong-password")));
		expectFail("존재하지 않는 사용자로 로그인",
				() -> authService.login(new UserLoginRequest("nobody", "password456")));

		expectFail("이미 UserStatus가 있는 사용자의 UserStatus 생성",
				() -> userStatusService.create(new UserStatusCreateRequest(user1.id(), null)));
		expectFail("존재하지 않는 사용자의 UserStatus 생성",
				() -> userStatusService.create(new UserStatusCreateRequest(UUID.randomUUID(), null)));

		printFile("userStatuses.ser");

		// =========================
		// 3. Channel 테스트
		// =========================
		System.out.println("\n===== 3. Channel 테스트 =====");

		ChannelDto publicChannel = channelService.createPublic(
				new PublicChannelCreateRequest(user1.id(), "general", "일반 채널"));
		ChannelDto privateChannel = channelService.createPrivate(
				new PrivateChannelCreateRequest(user1.id(), List.of(user2.id())));

		System.out.println("PUBLIC 채널: " + publicChannel.name() + " / 타입: " + publicChannel.type());
		System.out.println("PRIVATE 채널 이름: " + privateChannel.name() + " / 타입: " + privateChannel.type());
		System.out.println("PRIVATE 참여자 수: " + privateChannel.participantIds().size());
		System.out.println("PRIVATE 생성 시 ReadStatus 생성 (user2): "
				+ readStatusService.findAllByUserId(user2.id()).size() + "건");

		// findAllByUserId: PUBLIC 전체 + 참여한 PRIVATE
		System.out.println("user2가 볼 수 있는 채널 수 (PUBLIC + 참여 PRIVATE): "
				+ channelService.findAllByUserId(user2.id()).size());
		System.out.println("user3가 볼 수 있는 채널 수 (PUBLIC만): "
				+ channelService.findAllByUserId(user3.id()).size());

		// 입장
		channelService.join(publicChannel.id(), user2.id());
		System.out.println("PUBLIC 입장 후 멤버 수: " + userService.findAllByChannelId(publicChannel.id()).size());
		expectFail("PRIVATE 채널 입장",
				() -> channelService.join(privateChannel.id(), user3.id()));

		// 수정
		ChannelDto updatedChannel = channelService.update(publicChannel.id(),
				new ChannelUpdateRequest(user1.id(), "general-updated", "수정된 설명"));
		System.out.println("PUBLIC 채널 수정: " + updatedChannel.name() + " / " + updatedChannel.description());
		expectFail("PRIVATE 채널 수정",
				() -> channelService.update(privateChannel.id(), new ChannelUpdateRequest(user1.id(), "x", "x")));
		expectFail("소유자가 아닌 사용자의 채널 수정",
				() -> channelService.update(publicChannel.id(), new ChannelUpdateRequest(user2.id(), "hacked", "x")));

		printFile("channels.ser");

		// =========================
		// 4. Message / BinaryContent 테스트
		// =========================
		System.out.println("\n===== 4. Message / BinaryContent 테스트 =====");

		MessageDto plainMessage = messageService.create(
				new MessageCreateRequest(user1.id(), publicChannel.id(), "안녕하세요!"),
				List.of());
		MessageDto fileMessage = messageService.create(
				new MessageCreateRequest(user2.id(), publicChannel.id(), "파일 2개 첨부합니다"),
				List.of(image("a.png"), image("b.png")));

		System.out.println("첨부파일 없는 메시지: " + plainMessage.attachmentIds().size() + "개");
		System.out.println("첨부파일 있는 메시지: " + fileMessage.attachmentIds().size() + "개");
		System.out.println("첨부파일 일괄 조회 (findAllByIdIn): "
				+ binaryContentService.findAllByIdIn(fileMessage.attachmentIds()).size() + "개");
		System.out.println("채널 메시지 수 (findAllByChannelId): "
				+ messageService.findAllByChannelId(publicChannel.id()).size());
		System.out.println("채널 최근 메시지 시간 포함: "
				+ (channelService.findById(publicChannel.id()).lastMessageAt() != null));

		expectFail("채널 멤버가 아닌 사용자의 메시지 작성",
				() -> messageService.create(new MessageCreateRequest(user3.id(), publicChannel.id(), "몰래 작성"), List.of()));
		expectFail("존재하지 않는 채널에 메시지 작성",
				() -> messageService.create(new MessageCreateRequest(user1.id(), UUID.randomUUID(), "없는 채널"), List.of()));

		MessageDto updatedMessage = messageService.update(plainMessage.id(),
				new MessageUpdateRequest(user1.id(), "수정된 메시지입니다."));
		System.out.println("메시지 수정: " + updatedMessage.content());
		expectFail("다른 사용자의 메시지 수정",
				() -> messageService.update(plainMessage.id(), new MessageUpdateRequest(user2.id(), "권한 없는 수정")));

		// 메시지 삭제 시 첨부파일도 함께 삭제
		List<UUID> attachmentIds = fileMessage.attachmentIds();
		messageService.delete(fileMessage.id(), user2.id());
		System.out.println("메시지 삭제 후 첨부파일 남은 개수: "
				+ binaryContentService.findAllByIdIn(attachmentIds).size());
		expectFail("삭제된 메시지 조회",
				() -> messageService.findById(fileMessage.id()));

		// BinaryContentService 단독 사용
		BinaryContentDto standalone = binaryContentService.create(image("standalone.png"));
		System.out.println("BinaryContent 단독 생성: " + standalone.fileName() + " (" + standalone.size() + " bytes)");
		binaryContentService.delete(standalone.id());
		expectFail("삭제된 BinaryContent 조회",
				() -> binaryContentService.findById(standalone.id()));

		printFile("messages.ser");
		printFile("binaryContents.ser");

		// =========================
		// 5. ReadStatus 테스트
		// =========================
		System.out.println("\n===== 5. ReadStatus 테스트 =====");

		ReadStatusDto readStatus = readStatusService.create(
				new ReadStatusCreateRequest(user2.id(), publicChannel.id(), null));
		System.out.println("ReadStatus 생성: lastReadAt 자동 채움 = " + (readStatus.lastReadAt() != null));

		expectFail("같은 사용자·채널 ReadStatus 중복 생성",
				() -> readStatusService.create(new ReadStatusCreateRequest(user2.id(), publicChannel.id(), null)));
		expectFail("존재하지 않는 채널의 ReadStatus 생성",
				() -> readStatusService.create(new ReadStatusCreateRequest(user2.id(), UUID.randomUUID(), null)));

		Instant readAt = Instant.now().plusSeconds(60);
		ReadStatusDto updatedReadStatus = readStatusService.update(readStatus.id(), new ReadStatusUpdateRequest(readAt));
		System.out.println("ReadStatus 수정: " + updatedReadStatus.lastReadAt().equals(readAt));

		readStatusService.delete(readStatus.id());
		expectFail("삭제된 ReadStatus 조회",
				() -> readStatusService.findById(readStatus.id()));

		printFile("readStatuses.ser");

		// =========================
		// 6. 삭제 연쇄 테스트
		// =========================
		System.out.println("\n===== 6. 삭제 연쇄 테스트 =====");

		// 채널 삭제 → 메시지, ReadStatus 함께 삭제
		messageService.create(new MessageCreateRequest(user1.id(), privateChannel.id(), "DM 메시지"), List.of());
		channelService.delete(privateChannel.id(), user1.id());
		System.out.println("PRIVATE 채널 삭제 후 메시지 수: "
				+ messageService.findAllByChannelId(privateChannel.id()).size());
		System.out.println("PRIVATE 채널 삭제 후 user2의 ReadStatus 수: "
				+ readStatusService.findAllByUserId(user2.id()).size());
		expectFail("소유자가 아닌 사용자의 채널 삭제",
				() -> channelService.delete(publicChannel.id(), user2.id()));

		// 사용자 삭제 → 프로필, UserStatus 함께 삭제
		UUID user3ProfileTarget = userService.update(user3.id(),
				new UserUpdateRequest(null, null, null), Optional.of(image("profile3.png"))).profileId();
		int statusCountBefore = userStatusService.findAll().size();
		userService.delete(user3.id());
		System.out.println("사용자 삭제 후 UserStatus 감소: " + (userStatusService.findAll().size() == statusCountBefore - 1));
		expectFail("삭제된 사용자의 프로필 조회",
				() -> binaryContentService.findById(user3ProfileTarget));
		expectFail("삭제된 사용자 조회",
				() -> userService.findById(user3.id()));

		System.out.println("\n최종 사용자 수: " + userService.findAll().size());
		System.out.println("최종 user1이 볼 수 있는 채널 수: " + channelService.findAllByUserId(user1.id()).size());

		System.out.println("\n===== 테스트 종료 =====");
	}

	// 테스트용 이미지 파일 데이터
	private static BinaryContentCreateRequest image(String fileName) {
		return new BinaryContentCreateRequest(fileName, "image/png", new byte[]{1, 2, 3, 4});
	}

	// 예외가 발생해야 정상인 상황을 검증하는 헬퍼
	// IllegalArgumentException, IllegalStateException 모두 잡기 위해 RuntimeException으로 받음
	private static void expectFail(String label, Runnable action) {
		try {
			action.run();
			System.out.println("실패: " + label + "이(가) 허용됨");
		} catch (RuntimeException e) {
			System.out.println(label + " 예외 확인: " + e.getMessage());
		}
	}

	// data/ 디렉터리의 내부 파일들 미리 지우기
	private static void cleanData() {
		try {
			for (String fileName : DATA_FILES) {
				Files.deleteIfExists(Paths.get("data", fileName));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static void printFile(String fileName) {
		Path path = Paths.get("data", fileName);
		if (!Files.exists(path)) {
			System.out.println("[" + fileName + "] 파일 없음");
			return;
		}
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
			Map<UUID, ?> data = (Map<UUID, ?>) ois.readObject();
			System.out.println("[" + fileName + "] " + data.size() + "건  : 파일에 저장된 객체");
			data.values().forEach(value -> System.out.println("  " + value));
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}