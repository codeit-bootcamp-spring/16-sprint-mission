package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;

import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context =
				SpringApplication.run(
						DiscodeitApplication.class,
						args
				);

		UserService userService =
				context.getBean(UserService.class);

		ChannelService channelService =
				context.getBean(ChannelService.class);

		MessageService messageService =
				context.getBean(MessageService.class);

		ReadStatusService readStatusService =
				context.getBean(ReadStatusService.class);

		UserStatusService userStatusService =
				context.getBean(UserStatusService.class);

		BinaryContentService binaryContentService =
				context.getBean(BinaryContentService.class);

		AuthService authService =
				context.getBean(AuthService.class);

		UserRepository userRepository =
				context.getBean(UserRepository.class);

		String repositoryName =
				userRepository.getClass().getSimpleName();

		System.out.println();
		System.out.println("================================");
		System.out.println("Discodeit Spring Boot 시작");
		System.out.println("Repository: " + repositoryName);
		System.out.println("================================");

		runScenario(
				"Spring Context - " + repositoryName,
				userService,
				channelService,
				messageService,
				readStatusService,
				userStatusService,
				binaryContentService,
				authService
		);
	}

	static void runScenario(
			String scenarioName,
			UserService userService,
			ChannelService channelService,
			MessageService messageService,
			ReadStatusService readStatusService,
			UserStatusService userStatusService,
			BinaryContentService binaryContentService,
			AuthService authService
	) {

		System.out.println();
		System.out.println("시나리오 시작: " + scenarioName);

		String unique = UUID.randomUUID()
				.toString()
				.substring(0, 8);

		System.out.println();
		System.out.println("1. 사용자 생성 테스트");

		UserResponse firstUser = userService.create(
				new CreateUserRequest(
						"장치욱_" + unique,
						unique + "a@test.com",
						"7319",
						new CreateBinaryContentRequest(
								"profile.png",
								"image/png",
								new byte[]{1, 2, 3}
						)
				)
		);

		UserResponse secondUser = userService.create(
				new CreateUserRequest(
						"장시우_" + unique,
						unique + "b@test.com",
						"7937",
						null
				)
		);

		UserResponse guestUser = userService.create(
				new CreateUserRequest(
						"방문자_" + unique,
						unique + "c@test.com",
						"1234",
						null
				)
		);

		verify(
				firstUser.id() != null
						&& secondUser.id() != null
						&& guestUser.id() != null,
				"사용자 생성 실패"
		);

		System.out.println(
				"생성 완료: " + firstUser.username()
		);
		System.out.println(
				"생성 완료: " + secondUser.username()
		);
		System.out.println(
				"생성 완료: " + guestUser.username()
		);

		System.out.println();
		System.out.println("2. 사용자 DTO 조회 테스트");

		UserResponse foundUser =
				userService.findById(firstUser.id());

		verify(
				foundUser.id().equals(firstUser.id()),
				"사용자 단건 조회 실패"
		);

		List<UserResponse> allUsers =
				userService.findAll();

		verify(
				allUsers.stream()
						.anyMatch(user ->
								user.id().equals(firstUser.id())
						),
				"사용자 전체 조회 실패"
		);

		boolean passwordExposed = false;

		for (var component :
				UserResponse.class.getRecordComponents()) {

			if (component.getName()
					.equalsIgnoreCase("password")) {

				passwordExposed = true;
			}
		}

		verify(
				!passwordExposed,
				"UserResponse에 비밀번호가 포함되어 있습니다."
		);

		System.out.println(
				"UserResponse 단건/전체 조회 성공"
		);

		System.out.println();
		System.out.println("3. 로그인 및 중복 검증");

		UserResponse loginUser = authService.login(
				new LoginRequest(
						firstUser.username(),
						"7319"
				)
		);

		verify(
				loginUser.id().equals(firstUser.id()),
				"로그인 실패"
		);

		expectFailure(
				"잘못된 비밀번호",
				() -> authService.login(
						new LoginRequest(
								firstUser.username(),
								"wrong"
						)
				)
		);

		expectFailure(
				"중복 사용자 이름",
				() -> userService.create(
						new CreateUserRequest(
								firstUser.username(),
								unique + "other@test.com",
								"1234",
								null
						)
				)
		);

		expectFailure(
				"중복 이메일",
				() -> userService.create(
						new CreateUserRequest(
								"새사용자_" + unique,
								firstUser.email(),
								"1234",
								null
						)
				)
		);

		System.out.println(
				"로그인 및 사용자 중복 검증 성공"
		);

		System.out.println();
		System.out.println("4. 사용자 수정 테스트");

		UserResponse updatedSecondUser =
				userService.update(
						new UpdateUserRequest(
								secondUser.id(),
								"장시우수정_" + unique,
								unique + "updated@test.com",
								"7937",
								null
						)
				);

		verify(
				updatedSecondUser.username()
						.equals("장시우수정_" + unique),
				"사용자 이름 수정 실패"
		);

		verify(
				userService.findById(secondUser.id())
						.email()
						.equals(unique + "updated@test.com"),
				"사용자 이메일 수정 실패"
		);

		System.out.println(
				"사용자 수정 및 DTO 조회 성공"
		);

		System.out.println();
		System.out.println("5. 사용자 접속 상태 테스트");

		UserStatusResponse offlineStatus =
				userStatusService.updateByUserId(
						new UpdateUserStatusByUserIdRequest(
								firstUser.id(),
								Instant.now().minus(
										6,
										ChronoUnit.MINUTES
								)
						)
				);

		verify(
				!offlineStatus.online(),
				"오프라인 상태 검증 실패"
		);

		verify(
				!userService.findById(firstUser.id()).online(),
				"사용자 DTO의 오프라인 정보가 올바르지 않습니다."
		);

		UserStatusResponse onlineStatus =
				userStatusService.update(
						new UpdateUserStatusRequest(
								offlineStatus.id(),
								Instant.now()
						)
				);

		verify(
				onlineStatus.online(),
				"온라인 상태 검증 실패"
		);

		verify(
				userService.findById(firstUser.id()).online(),
				"사용자 DTO의 온라인 정보가 올바르지 않습니다."
		);

		System.out.println(
				"UserStatus DTO 조회 및 수정 성공"
		);

		System.out.println();
		System.out.println("6. PUBLIC 채널 생성 테스트");

		ChannelResponse publicChannel =
				channelService.createPublic(
						new CreatePublicChannelRequest(
								"공지_" + unique,
								"공지사항 채널"
						)
				);

		verify(
				publicChannel.id() != null,
				"PUBLIC 채널 생성 실패"
		);

		verify(
				channelService.findById(publicChannel.id())
						.id()
						.equals(publicChannel.id()),
				"PUBLIC 채널 DTO 조회 실패"
		);

		System.out.println(
				"PUBLIC 채널 생성 및 조회 성공"
		);

		System.out.println();
		System.out.println("7. PUBLIC 채널 수정 테스트");

		ChannelResponse updatedPublicChannel =
				channelService.update(
						new UpdateChannelRequest(
								publicChannel.id(),
								"공지수정_" + unique,
								"수정된 공지사항 채널"
						)
				);

		verify(
				updatedPublicChannel.name()
						.equals("공지수정_" + unique),
				"PUBLIC 채널 이름 수정 실패"
		);

		verify(
				channelService.findById(publicChannel.id())
						.description()
						.equals("수정된 공지사항 채널"),
				"PUBLIC 채널 설명 수정 실패"
		);

		System.out.println(
				"PUBLIC 채널 수정 및 DTO 조회 성공"
		);

		System.out.println();
		System.out.println("8. PRIVATE 채널 생성 테스트");

		ChannelResponse privateChannel =
				channelService.createPrivate(
						new CreatePrivateChannelRequest(
								Set.of(
										firstUser.id(),
										secondUser.id()
								)
						)
				);

		verify(
				privateChannel.participantIds().containsAll(
						Set.of(
								firstUser.id(),
								secondUser.id()
						)
				),
				"PRIVATE 채널 참여자 설정 실패"
		);

		verify(
				channelService.findAllByUserId(firstUser.id())
						.stream()
						.anyMatch(channel ->
								channel.id().equals(
										privateChannel.id()
								)
						),
				"참여자의 PRIVATE 채널 조회 실패"
		);

		verify(
				channelService.findAllByUserId(guestUser.id())
						.stream()
						.noneMatch(channel ->
								channel.id().equals(
										privateChannel.id()
								)
						),
				"비참여자에게 PRIVATE 채널이 노출됩니다."
		);

		verify(
				readStatusService.findAllByUserId(firstUser.id())
						.stream()
						.anyMatch(status ->
								status.channelId().equals(
										privateChannel.id()
								)
						),
				"PRIVATE 채널의 ReadStatus 생성 실패"
		);

		expectFailure(
				"PRIVATE 채널 수정 제한",
				() -> channelService.update(
						new UpdateChannelRequest(
								privateChannel.id(),
								"수정불가",
								"수정불가"
						)
				)
		);

		System.out.println(
				"PRIVATE 채널 생성, 참여자 조회 및 수정 제한 성공"
		);

		System.out.println();
		System.out.println("9. PUBLIC 채널 참여/탈퇴 테스트");

		channelService.joinChannel(
				publicChannel.id(),
				guestUser.id()
		);

		verify(
				readStatusService.findAllByUserId(guestUser.id())
						.stream()
						.anyMatch(status ->
								status.channelId().equals(
										publicChannel.id()
								)
						),
				"PUBLIC 채널 참여 실패"
		);

		channelService.leaveChannel(
				publicChannel.id(),
				guestUser.id()
		);

		verify(
				readStatusService.findAllByUserId(guestUser.id())
						.stream()
						.noneMatch(status ->
								status.channelId().equals(
										publicChannel.id()
								)
						),
				"PUBLIC 채널 탈퇴 실패"
		);

		channelService.joinChannel(
				publicChannel.id(),
				firstUser.id()
		);

		System.out.println(
				"PUBLIC 채널 참여 및 탈퇴 성공"
		);

		System.out.println();
		System.out.println("10. 메시지 및 첨부파일 생성");

		MessageResponse privateMessage =
				messageService.create(
						new CreateMessageRequest(
								"PRIVATE 채널 메시지",
								firstUser.id(),
								privateChannel.id(),
								List.of(
										new CreateBinaryContentRequest(
												"private.txt",
												"text/plain",
												new byte[]{1, 2, 3}
										)
								)
						)
				);

		MessageResponse publicMessage =
				messageService.create(
						new CreateMessageRequest(
								"PUBLIC 채널 메시지",
								firstUser.id(),
								publicChannel.id(),
								List.of(
										new CreateBinaryContentRequest(
												"public.txt",
												"text/plain",
												new byte[]{4, 5, 6}
										)
								)
						)
				);

		UUID privateAttachmentId =
				privateMessage.attachmentIds()
						.iterator()
						.next();

		UUID publicAttachmentId =
				publicMessage.attachmentIds()
						.iterator()
						.next();

		verify(
				messageService.findById(privateMessage.id())
						.id()
						.equals(privateMessage.id()),
				"메시지 DTO 단건 조회 실패"
		);

		verify(
				messageService.findAllByChannelId(
						privateChannel.id()
				).stream().anyMatch(message ->
						message.id().equals(privateMessage.id())
				),
				"채널별 메시지 DTO 조회 실패"
		);

		verify(
				binaryContentService.findById(privateAttachmentId)
						.fileName()
						.equals("private.txt"),
				"첨부파일 DTO 조회 실패"
		);

		verify(
				binaryContentService.findAllByIdIn(
						List.of(privateAttachmentId)
				).size() == 1,
				"첨부파일 목록 조회 실패"
		);

		verify(
				channelService.findById(privateChannel.id())
						.lastMessageAt() != null,
				"채널의 최근 메시지 시간 조회 실패"
		);

		System.out.println(
				"메시지 생성 및 첨부파일 조회 성공"
		);

		System.out.println();
		System.out.println("11. 메시지 수정 테스트");

		MessageResponse updatedMessage =
				messageService.update(
						new UpdateMessageRequest(
								privateMessage.id(),
								"수정된 PRIVATE 메시지"
						)
				);

		verify(
				updatedMessage.content()
						.equals("수정된 PRIVATE 메시지"),
				"메시지 수정 실패"
		);

		System.out.println(
				"메시지 수정 및 DTO 응답 성공"
		);

		System.out.println();
		System.out.println("12. ReadStatus 수정 테스트");

		ReadStatusResponse readStatus =
				readStatusService.findAllByUserId(firstUser.id())
						.stream()
						.filter(status ->
								status.channelId().equals(
										privateChannel.id()
								)
						)
						.findFirst()
						.orElseThrow();

		ReadStatusResponse updatedReadStatus =
				readStatusService.update(
						new UpdateReadStatusRequest(
								readStatus.id(),
								Instant.now()
						)
				);

		verify(
				updatedReadStatus.id().equals(readStatus.id()),
				"ReadStatus 수정 실패"
		);

		expectFailure(
				"ReadStatus 중복 생성",
				() -> readStatusService.create(
						new CreateReadStatusRequest(
								firstUser.id(),
								privateChannel.id(),
								Instant.now()
						)
				)
		);

		System.out.println(
				"ReadStatus 수정 및 중복 검증 성공"
		);

		System.out.println();
		System.out.println("13. 채널 참여 중 사용자 삭제");

		UUID deletedUserId = firstUser.id();
		UUID deletedProfileId = firstUser.profileId();

		userService.delete(deletedUserId);

		expectFailure(
				"삭제한 사용자 조회",
				() -> userService.findById(deletedUserId)
		);

		verify(
				!channelService.findById(privateChannel.id())
						.participantIds()
						.contains(deletedUserId),
				"삭제한 사용자의 PRIVATE 참여 관계가 남아 있습니다."
		);

		verify(
				channelService.findById(privateChannel.id())
						.participantIds()
						.contains(secondUser.id()),
				"남아 있는 참여자의 관계가 손상되었습니다."
		);

		verify(
				userStatusService.findAll()
						.stream()
						.noneMatch(status ->
								status.userId().equals(deletedUserId)
						),
				"UserStatus 연쇄 삭제 실패"
		);

		expectFailure(
				"프로필 연쇄 삭제",
				() -> binaryContentService.findById(
						deletedProfileId
				)
		);

		System.out.println(
				"채널 참여 중 사용자 삭제 및 관계 정리 성공"
		);

		System.out.println();
		System.out.println("14. 작성자 없는 메시지 조회");

		MessageResponse orphanMessage =
				messageService.findById(privateMessage.id());

		verify(
				orphanMessage.userId().equals(deletedUserId),
				"작성자 삭제 후 메시지 조회 실패"
		);

		verify(
				orphanMessage.content()
						.equals("수정된 PRIVATE 메시지"),
				"작성자 삭제 후 메시지 내용 손상"
		);

		System.out.println(
				"삭제된 작성자의 메시지 조회 성공"
		);

		System.out.println();
		System.out.println("15. 작성자 없는 메시지 삭제");

		messageService.delete(privateMessage.id());

		expectFailure(
				"메시지 삭제 확인",
				() -> messageService.findById(
						privateMessage.id()
				)
		);

		expectFailure(
				"첨부파일 연쇄 삭제 확인",
				() -> binaryContentService.findById(
						privateAttachmentId
				)
		);

		System.out.println(
				"작성자 없는 메시지 삭제 성공"
		);

		System.out.println();
		System.out.println("16. PUBLIC 채널 삭제");

		channelService.delete(publicChannel.id());

		expectFailure(
				"PUBLIC 채널 삭제 확인",
				() -> channelService.findById(
						publicChannel.id()
				)
		);

		expectFailure(
				"PUBLIC 메시지 첨부파일 연쇄 삭제",
				() -> binaryContentService.findById(
						publicAttachmentId
				)
		);

		System.out.println(
				"PUBLIC 채널 및 메시지 정리 성공"
		);

		System.out.println();
		System.out.println("17. PRIVATE 채널 삭제");

		channelService.delete(privateChannel.id());

		expectFailure(
				"PRIVATE 채널 삭제 확인",
				() -> channelService.findById(
						privateChannel.id()
				)
		);

		verify(
				channelService.findAllByUserId(secondUser.id())
						.stream()
						.noneMatch(channel ->
								channel.id().equals(
										privateChannel.id()
								)
						),
				"PRIVATE 채널 삭제 후 참여 관계 정리 실패"
		);

		System.out.println(
				"PRIVATE 채널 및 ReadStatus 정리 성공"
		);

		System.out.println();
		System.out.println("18. 남아 있는 사용자 삭제");

		userService.delete(secondUser.id());
		userService.delete(guestUser.id());

		verify(
				userService.findAll()
						.stream()
						.noneMatch(user ->
								user.id().equals(firstUser.id())
										|| user.id().equals(secondUser.id())
										|| user.id().equals(guestUser.id())
						),
				"사용자 최종 삭제 확인 실패"
		);

		System.out.println(
				"사용자 전체 정리 성공"
		);

		System.out.println();
		System.out.println("================================");
		System.out.println("전체 시나리오 테스트 성공");
		System.out.println(scenarioName);
		System.out.println("================================");
	}

	private static void verify(
			boolean condition,
			String errorMessage
	) {
		if (!condition) {
			throw new IllegalStateException(errorMessage);
		}
	}

	private static void expectFailure(
			String testName,
			Runnable action
	) {
		try {
			action.run();
		} catch (IllegalArgumentException
				 | IllegalStateException expected) {

			System.out.println(
					testName + " : 성공"
			);

			return;
		}

		throw new IllegalStateException(
				testName + " : 실패 (예외가 발생하지 않았습니다.)"
		);
	}
}