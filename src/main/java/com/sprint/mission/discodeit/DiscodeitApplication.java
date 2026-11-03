package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserStatusResponse;

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
import java.util.Arrays;
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
		System.out.println("======================================");
		System.out.println("Discodeit Spring Boot 애플리케이션 실행");
		System.out.println("======================================");

		System.out.println(
				"현재 Repository 구현체: " + repositoryName
		);

		runScenario(
				"Spring Context + " + repositoryName,
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
		System.out.println("======================================");
		System.out.println("시나리오 테스트 시작");
		System.out.println(scenarioName);
		System.out.println("======================================");

		String unique =
				UUID.randomUUID().toString().substring(0, 8);

		String firstUsername = "장치욱_" + unique;
		String secondUsername = "장시우_" + unique;
		String guestUsername = "방문자_" + unique;

		System.out.println();
		System.out.println("[1] 사용자 생성");

		UserResponse firstUser = userService.create(
				new CreateUserRequest(
						firstUsername,
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
						secondUsername,
						unique + "b@test.com",
						"7937",
						null
				)
		);

		UserResponse guestUser = userService.create(
				new CreateUserRequest(
						guestUsername,
						unique + "c@test.com",
						"1234",
						null
				)
		);

		System.out.println(
				"첫 번째 사용자 생성: " + firstUser.username()
		);

		System.out.println(
				"두 번째 사용자 생성: " + secondUser.username()
		);

		System.out.println(
				"세 번째 사용자 생성: " + guestUser.username()
		);

		System.out.println();
		System.out.println("[2] 사용자 DTO 조회");

		UserResponse foundUser =
				userService.findById(firstUser.id());

		verify(
				foundUser.id().equals(firstUser.id()),
				"사용자 단건 조회 실패"
		);

		verify(
				userService.findAll()
						.stream()
						.anyMatch(user ->
								user.id().equals(firstUser.id())
						),
				"사용자 전체 조회 실패"
		);

		verify(
				Arrays.stream(
						UserResponse.class.getRecordComponents()
				).noneMatch(component ->
						component.getName()
								.equalsIgnoreCase("password")
				),
				"UserResponse에 비밀번호가 포함되어 있습니다."
		);

		System.out.println(
				"사용자 단건/전체 DTO 조회 성공"
		);

		System.out.println(
				"UserResponse 비밀번호 제외 확인"
		);

		System.out.println();
		System.out.println("[3] 로그인 및 중복 검증");

		UserResponse loginUser = authService.login(
				new LoginRequest(
						firstUsername,
						"7319"
				)
		);

		verify(
				loginUser.id().equals(firstUser.id()),
				"로그인 실패"
		);

		expectFailure(
				"잘못된 비밀번호 로그인",
				() -> authService.login(
						new LoginRequest(
								firstUsername,
								"wrong"
						)
				)
		);

		expectFailure(
				"중복 사용자 이름",
				() -> userService.create(
						new CreateUserRequest(
								firstUsername,
								unique + "x@test.com",
								"1234",
								null
						)
				)
		);

		expectFailure(
				"중복 이메일",
				() -> userService.create(
						new CreateUserRequest(
								"other_" + unique,
								unique + "a@test.com",
								"1234",
								null
						)
				)
		);

		System.out.println(
				"로그인 및 사용자 중복 검증 성공"
		);

		System.out.println();
		System.out.println("[4] UserStatus 확인");

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
				"오프라인 판정 실패"
		);

		verify(
				!userService.findById(firstUser.id()).online(),
				"사용자 DTO 오프라인 상태 조회 실패"
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
				"온라인 판정 실패"
		);

		verify(
				userService.findById(firstUser.id()).online(),
				"사용자 DTO 온라인 상태 조회 실패"
		);

		System.out.println(
				"온라인 및 오프라인 상태 확인 성공"
		);

		System.out.println();
		System.out.println("[5] PUBLIC 채널 생성");

		ChannelResponse publicChannel =
				channelService.createPublic(
						new CreatePublicChannelRequest(
								"공지_" + unique,
								"공지사항을 공유하는 채널"
						)
				);

		verify(
				channelService.findById(publicChannel.id())
						.id()
						.equals(publicChannel.id()),
				"PUBLIC 채널 조회 실패"
		);

		System.out.println(
				"PUBLIC 채널 생성 성공: "
						+ publicChannel.name()
		);

		System.out.println();
		System.out.println("[6] PRIVATE 채널 생성");

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
				privateChannel.participantIds()
						.containsAll(
								Set.of(
										firstUser.id(),
										secondUser.id()
								)
						),
				"PRIVATE 채널 참여자 확인 실패"
		);

		verify(
				channelService.findAllByUserId(guestUser.id())
						.stream()
						.noneMatch(channel ->
								channel.id().equals(
										privateChannel.id()
								)
						),
				"참여하지 않은 사용자에게 PRIVATE 채널이 노출됩니다."
		);

		verify(
				channelService.findAllByUserId(firstUser.id())
						.stream()
						.anyMatch(channel ->
								channel.id().equals(
										privateChannel.id()
								)
						),
				"PRIVATE 채널 참여자 조회 실패"
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

		System.out.println(
				"PRIVATE 채널 생성 및 참여자 검증 성공"
		);

		System.out.println();
		System.out.println("[7] PUBLIC 채널 참여");

		channelService.joinChannel(
				publicChannel.id(),
				firstUser.id()
		);

		verify(
				readStatusService.findAllByUserId(firstUser.id())
						.stream()
						.anyMatch(status ->
								status.channelId().equals(
										publicChannel.id()
								)
						),
				"PUBLIC 채널 참여 시 ReadStatus 생성 실패"
		);

		System.out.println(
				"PUBLIC 채널 참여 및 ReadStatus 생성 성공"
		);

		System.out.println();
		System.out.println("[8] 메시지 및 첨부파일 생성");

		MessageResponse privateMessage =
				messageService.create(
						new CreateMessageRequest(
								"PRIVATE 채널 첫 번째 메시지",
								firstUser.id(),
								privateChannel.id(),
								List.of(
										new CreateBinaryContentRequest(
												"private.txt",
												"text/plain",
												new byte[]{9, 8, 7}
										)
								)
						)
				);

		MessageResponse publicMessage =
				messageService.create(
						new CreateMessageRequest(
								"PUBLIC 채널 첫 번째 메시지",
								firstUser.id(),
								publicChannel.id(),
								List.of(
										new CreateBinaryContentRequest(
												"public.txt",
												"text/plain",
												new byte[]{6, 5, 4}
										)
								)
						)
				);

		verify(
				messageService.findById(privateMessage.id())
						.id()
						.equals(privateMessage.id()),
				"메시지 단건 DTO 조회 실패"
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
				channelService.findById(privateChannel.id())
						.lastMessageAt() != null,
				"채널의 마지막 메시지 시간 조회 실패"
		);

		UUID privateAttachmentId =
				privateMessage.attachmentIds()
						.iterator()
						.next();

		UUID publicAttachmentId =
				publicMessage.attachmentIds()
						.iterator()
						.next();

		BinaryContentResponse foundAttachment =
				binaryContentService.findById(
						privateAttachmentId
				);

		verify(
				foundAttachment.fileName().equals("private.txt"),
				"첨부파일 조회 실패"
		);

		verify(
				binaryContentService.findAllByIdIn(
						List.of(privateAttachmentId)
				).size() == 1,
				"첨부파일 목록 조회 실패"
		);

		System.out.println(
				"메시지 및 첨부파일 생성/조회 성공"
		);

		System.out.println();
		System.out.println("[9] 메시지 수정");

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
				"메시지 수정 및 DTO 응답 확인 성공"
		);

		System.out.println();
		System.out.println("[10] ReadStatus 수정 및 중복 검증");

		ReadStatusResponse privateReadStatus =
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
								privateReadStatus.id(),
								Instant.now()
						)
				);

		verify(
				updatedReadStatus.id()
						.equals(privateReadStatus.id()),
				"ReadStatus 수정 실패"
		);

		expectFailure(
				"중복 ReadStatus 생성",
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
		System.out.println("[11] 채널 참여 중인 사용자 삭제");

		UUID firstUserId = firstUser.id();
		UUID firstProfileId = firstUser.profileId();

		userService.delete(firstUserId);

		expectFailure(
				"삭제된 사용자 조회",
				() -> userService.findById(firstUserId)
		);

		verify(
				channelService.findById(privateChannel.id())
						.participantIds()
						.stream()
						.noneMatch(userId ->
								userId.equals(firstUserId)
						),
				"삭제된 사용자의 PRIVATE 채널 참여 관계가 남아 있습니다."
		);

		verify(
				channelService.findById(privateChannel.id())
						.participantIds()
						.contains(secondUser.id()),
				"남아 있는 사용자의 PRIVATE 채널 참여 관계가 손상되었습니다."
		);

		verify(
				userStatusService.findAll()
						.stream()
						.noneMatch(status ->
								status.userId().equals(firstUserId)
						),
				"사용자 삭제 시 UserStatus 정리 실패"
		);

		verify(
				readStatusService.findAllByUserId(secondUser.id())
						.stream()
						.anyMatch(status ->
								status.channelId().equals(
										privateChannel.id()
								)
						),
				"다른 사용자의 ReadStatus가 손상되었습니다."
		);

		expectFailure(
				"삭제된 프로필 조회",
				() -> binaryContentService.findById(
						firstProfileId
				)
		);

		System.out.println(
				"사용자 삭제 및 연관 데이터 정리 성공"
		);

		System.out.println();
		System.out.println("[12] 작성자가 삭제된 메시지 확인");

		MessageResponse orphanMessage =
				messageService.findById(
						privateMessage.id()
				);

		verify(
				orphanMessage.userId().equals(firstUserId),
				"작성자가 삭제된 메시지를 조회할 수 없습니다."
		);

		verify(
				orphanMessage.content()
						.equals("수정된 PRIVATE 메시지"),
				"작성자가 삭제된 메시지 내용이 변경되었습니다."
		);

		System.out.println(
				"작성자가 삭제된 메시지 조회 성공"
		);

		System.out.println();
		System.out.println("[13] 작성자 없는 메시지 삭제");

		messageService.delete(
				privateMessage.id()
		);

		expectFailure(
				"삭제된 메시지 조회",
				() -> messageService.findById(
						privateMessage.id()
				)
		);

		expectFailure(
				"메시지 첨부파일 연쇄 삭제",
				() -> binaryContentService.findById(
						privateAttachmentId
				)
		);

		System.out.println(
				"작성자 없는 메시지 삭제 및 첨부파일 정리 성공"
		);

		System.out.println();
		System.out.println("[14] 채널 삭제");

		channelService.delete(
				publicChannel.id()
		);

		expectFailure(
				"PUBLIC 채널 삭제",
				() -> channelService.findById(
						publicChannel.id()
				)
		);

		expectFailure(
				"PUBLIC 채널 메시지 첨부파일 삭제",
				() -> binaryContentService.findById(
						publicAttachmentId
				)
		);

		channelService.delete(
				privateChannel.id()
		);

		expectFailure(
				"PRIVATE 채널 삭제",
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
				"PRIVATE 채널 삭제 후 사용자 관계 정리 실패"
		);

		System.out.println(
				"채널 및 관련 데이터 삭제 성공"
		);

		System.out.println();
		System.out.println("[15] 남은 사용자 삭제");

		userService.delete(secondUser.id());
		userService.delete(guestUser.id());

		expectFailure(
				"두 번째 사용자 삭제 확인",
				() -> userService.findById(secondUser.id())
		);

		expectFailure(
				"세 번째 사용자 삭제 확인",
				() -> userService.findById(guestUser.id())
		);

		System.out.println(
				"남은 사용자 정리 성공"
		);

		System.out.println();
		System.out.println("======================================");
		System.out.println("전체 시나리오 테스트 성공");
		System.out.println(scenarioName);
		System.out.println("======================================");
	}

	private static void verify(
			boolean condition,
			String message
	) {
		if (!condition) {
			throw new IllegalStateException(
					message
			);
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
					testName + " 테스트 성공"
			);

			return;
		}

		throw new IllegalStateException(
				testName
						+ " 테스트 실패: 예상한 예외가 발생하지 않았습니다."
		);
	}
}