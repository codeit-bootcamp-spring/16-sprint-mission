package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.AuthUserDto;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);
        runVerificationScenarios(context);
    }

    /** 전체 검증을 독립 서비스, 사용자 인증, 채널과 메시지 순서로 실행합니다. */
    private static void runVerificationScenarios(
            ConfigurableApplicationContext context
    ) {
        startSection("Discodeit 통합 기능 검증 시작");
        verifyIndependentServices(context);

        UserDto user = verifyUserAndAuth(context);
        verifyChannelAndMessageServices(context, user);
        startSection("전체 검증 정상 종료");
        System.out.println("모든 검증 항목이 기대한 결과를 반환했습니다.");
    }

    private static void verifyIndependentServices(
            ConfigurableApplicationContext context
    ) {
        verifyBinaryContentService(
                context.getBean(BinaryContentService.class),
                context.getBean(BinaryContentRepository.class)
        );
        verifyUserStatusService(
                context.getBean(UserStatusService.class),
                context.getBean(UserRepository.class),
                context.getBean(UserStatusRepository.class)
        );
    }

    private static UserDto verifyUserAndAuth(
            ConfigurableApplicationContext context
    ) {
        UserService userService = context.getBean(UserService.class);
        BinaryContentRepository binaryContentRepository =
                context.getBean(BinaryContentRepository.class);
        UserStatusRepository userStatusRepository =
                context.getBean(UserStatusRepository.class);

        verifyUserService(userService, binaryContentRepository, userStatusRepository);

        UserDto user = setupUser(userService);
        verifyAuthService(context.getBean(AuthService.class), user);
        return user;
    }

    private static void verifyChannelAndMessageServices(
            ConfigurableApplicationContext context,
            UserDto user
    ) {
        ChannelService channelService = context.getBean(ChannelService.class);
        ReadStatusRepository readStatusRepository =
                context.getBean(ReadStatusRepository.class);
        BinaryContentRepository binaryContentRepository =
                context.getBean(BinaryContentRepository.class);
        Channel channel = setupChannel(channelService);
        logData("ReadStatus/메시지 검증용 공개 채널", "channelId=" + channel.getId()
                + ", type=" + channel.getType());

        verifyReadStatusService(
                context.getBean(ReadStatusService.class),
                user,
                channel,
                readStatusRepository
        );
        verifyChannelService(
                channelService,
                context.getBean(UserService.class),
                user,
                context.getBean(ChannelRepository.class),
                context.getBean(MessageRepository.class),
                readStatusRepository,
                binaryContentRepository
        );

        MessageService messageService = context.getBean(MessageService.class);
        verifyMessageService(
                messageService,
                user,
                channel,
                channelService,
                binaryContentRepository
        );
        messageCreateTest(messageService, channel, user);
    }

	static UserDto setupUser(UserService userService) {
		String runId = UUID.randomUUID().toString().substring(0, 8);
		String username = "woody-" + runId;
		String email = "woody-" + runId + "@codeit.com";

		UserCreateRequest request = new UserCreateRequest(
				username,
				email,
				"woody1234",
				null
		);

		return userService.create(request);
	}

	static Channel setupChannel(ChannelService channelService) {
		return channelService.createPublic(
				new PublicChannelCreateRequest("공지", "공지 채널입니다.")
		);
	}

	/** 바이너리 콘텐츠 생성, ID 조회, 여러 ID 조회, 삭제를 확인합니다. */
	private static void verifyBinaryContentService(
			BinaryContentService binaryContentService,
			BinaryContentRepository binaryContentRepository
	) {
		UUID createdContentId = null;
		try {
			startSection("BinaryContentService | 생성과 ID 조회");
			byte[] expectedData = new byte[]{10, 20, 30};
			var created = binaryContentService.create(new BinaryContentCreateRequest(
					"binary-check.bin", "application/octet-stream", expectedData
			));
			createdContentId = created.getId();
			UUID verifiedContentId = createdContentId;
            logData("생성 콘텐츠", "id=" + verifiedContentId
                    + ", fileName=" + created.getFileName()
                    + ", contentType=" + created.getContentType()
                    + ", dataSize=" + created.getData().length + " bytes");
			check(created.getFileName().equals("binary-check.bin")
					&& created.getContentType().equals("application/octet-stream")
					&& Arrays.equals(created.getData(), expectedData),
					"DTO 정보로 바이너리 콘텐츠 생성");
			check(binaryContentService.findById(verifiedContentId).getId()
					.equals(verifiedContentId), "ID로 바이너리 콘텐츠 조회");

			startSection("BinaryContentService | ID 목록 조회");
			UUID missingId = UUID.randomUUID();
			List<UUID> requestedIds = Arrays.asList(verifiedContentId, missingId);
			List<UUID> foundIds = binaryContentService.findAllByIdIn(requestedIds).stream()
					.map(content -> content.getId())
					.toList();
            logData("요청 ID 목록", requestedIds);
            logData("조회된 ID 목록", foundIds);
			check(foundIds.size() == 1 && foundIds.contains(verifiedContentId),
					"ID 목록 중 저장된 콘텐츠만 조회");

			startSection("BinaryContentService | 삭제");
            logData("삭제 대상 콘텐츠 ID", verifiedContentId);
			binaryContentService.delete(verifiedContentId);
			createdContentId = null;
			check(binaryContentRepository.findById(verifiedContentId).isEmpty(),
					"ID로 바이너리 콘텐츠 삭제");
		} finally {
			if (createdContentId != null) {
				binaryContentRepository.deleteById(createdContentId);
			}
		}
	}

	/** 사용자 상태의 생성, 조회, 갱신, 삭제 규칙을 확인합니다. */
	private static void verifyUserStatusService(
			UserStatusService userStatusService,
			UserRepository userRepository,
			UserStatusRepository userStatusRepository
	) {
		String runId = UUID.randomUUID().toString().substring(0, 8);
		User testUser = new User(
				"status-check-" + runId,
				"status-check-" + runId + "@codeit.com",
				"password"
		);
		userRepository.save(testUser);
		UUID createdStatusId = null;

		try {
			startSection("UserStatusService | 생성과 중복 방지");
			Instant initialActiveAt = Instant.now().minusSeconds(60);
			UserStatusCreateRequest createRequest = new UserStatusCreateRequest(
					testUser.getId(), initialActiveAt
			);
			UserStatus created = userStatusService.create(createRequest);
			createdStatusId = created.getId();
			UUID verifiedStatusId = createdStatusId;
            logData("UserStatus 생성", "userId=" + testUser.getId()
                    + ", statusId=" + verifiedStatusId
                    + ", lastActiveAt=" + created.getLastActiveAt());
			check(created.getUserId().equals(testUser.getId())
					&& created.getLastActiveAt().equals(initialActiveAt),
					"사용자 ID와 접속 시각으로 UserStatus 생성");
			expectIllegalArgument(
					() -> userStatusService.create(createRequest),
					"같은 사용자의 중복 UserStatus 생성 거부"
			);
			UUID missingUserId = UUID.randomUUID();
			logData("미존재 사용자와 연결한 UserStatus 요청", "userId=" + missingUserId
					+ ", lastActiveAt=현재 시각");
			expectIllegalArgument(
					() -> userStatusService.create(new UserStatusCreateRequest(
							missingUserId, Instant.now())),
					"존재하지 않는 사용자 상태 생성 거부"
			);

			startSection("UserStatusService | ID 및 전체 조회");
			check(userStatusService.findById(verifiedStatusId).getId().equals(verifiedStatusId),
					"ID로 UserStatus 조회");
			check(userStatusService.findAll().stream()
					.anyMatch(item -> item.getId().equals(verifiedStatusId)),
					"모든 UserStatus 조회 결과에 생성한 상태 포함");

			startSection("UserStatusService | ID 및 사용자 ID로 갱신");
			Instant updatedByIdAt = Instant.now().minusSeconds(20);
			userStatusService.update(new UserStatusUpdateRequest(
					verifiedStatusId, updatedByIdAt
			));
            logData("ID로 수정한 접속 시각", userStatusService.findById(verifiedStatusId).getLastActiveAt());
			check(userStatusService.findById(verifiedStatusId).getLastActiveAt()
					.equals(updatedByIdAt), "ID와 DTO로 마지막 접속 시각 수정");

			Instant updatedByUserIdAt = Instant.now();
			userStatusService.updateByUserId(testUser.getId(), updatedByUserIdAt);
            logData("userId로 수정한 접속 시각", userStatusService.findById(verifiedStatusId).getLastActiveAt());
			check(userStatusService.findById(verifiedStatusId).getLastActiveAt()
					.equals(updatedByUserIdAt), "userId로 해당 사용자의 상태 수정");

			startSection("UserStatusService | 삭제");
            logData("삭제 대상 UserStatus ID", verifiedStatusId);
			userStatusService.delete(verifiedStatusId);
			createdStatusId = null;
			check(userStatusRepository.findById(verifiedStatusId).isEmpty(),
					"ID로 UserStatus 삭제");
		} finally {
			if (createdStatusId != null) {
				userStatusRepository.deleteById(createdStatusId);
			}
			userRepository.deleteById(testUser.getId());
		}
	}

	/** 읽음 상태의 생성, 중복 방지, 조회, 수정, 삭제를 확인합니다. */
	private static void verifyReadStatusService(
			ReadStatusService readStatusService,
			UserDto user,
			Channel channel,
			ReadStatusRepository readStatusRepository
	) {
		UUID createdReadStatusId = null;
		try {
			startSection("ReadStatusService | 생성과 관계 확인");
			Instant initialReadAt = Instant.now().minusSeconds(30);
			ReadStatusCreateRequest createRequest = new ReadStatusCreateRequest(
					user.id(), channel.getId(), initialReadAt
			);
			ReadStatus created = readStatusService.create(createRequest);
			createdReadStatusId = created.getId();
			UUID verifiedReadStatusId = createdReadStatusId;
            logData("ReadStatus 생성", "readStatusId=" + verifiedReadStatusId
                    + ", userId=" + user.id()
                    + ", channelId=" + channel.getId()
                    + ", lastReadAt=" + created.getLastReadAt());
			check(created.getUserId().equals(user.id())
					&& created.getChannelId().equals(channel.getId())
					&& created.getLastReadAt().equals(initialReadAt),
					"사용자·채널·읽은 시각으로 ReadStatus 생성");
			expectIllegalArgument(
					() -> readStatusService.create(createRequest),
					"같은 사용자와 채널의 중복 ReadStatus 생성 거부"
			);

			UUID missingUserId = UUID.randomUUID();
			logData("미존재 사용자와 연결한 생성 요청", "userId=" + missingUserId
					+ ", channelId=" + channel.getId());
			expectIllegalArgument(
					() -> readStatusService.create(new ReadStatusCreateRequest(
							missingUserId, channel.getId(), Instant.now())),
					"존재하지 않는 사용자와 연결된 생성 거부"
			);
			UUID missingChannelId = UUID.randomUUID();
			logData("미존재 채널과 연결한 생성 요청", "userId=" + user.id()
					+ ", channelId=" + missingChannelId);
			expectIllegalArgument(
					() -> readStatusService.create(new ReadStatusCreateRequest(
							user.id(), missingChannelId, Instant.now())),
					"존재하지 않는 채널과 연결된 생성 거부"
			);

			startSection("ReadStatusService | ID 및 사용자별 조회");
			check(readStatusService.findById(verifiedReadStatusId).getId()
					.equals(verifiedReadStatusId), "ID로 ReadStatus 조회");
			check(readStatusService.findAllByUserId(user.id()).stream()
					.anyMatch(item -> item.getId().equals(verifiedReadStatusId)),
					"사용자 ID로 ReadStatus 목록 조회");

			startSection("ReadStatusService | 수정과 삭제");
			Instant updatedReadAt = Instant.now();
			readStatusService.update(new ReadStatusUpdateRequest(
					verifiedReadStatusId, updatedReadAt
			));
            logData("수정 후 lastReadAt", readStatusService.findById(verifiedReadStatusId).getLastReadAt());
			check(readStatusService.findById(verifiedReadStatusId).getLastReadAt()
					.equals(updatedReadAt), "마지막 읽은 시각 수정");

			readStatusService.delete(verifiedReadStatusId);
			createdReadStatusId = null;
			check(readStatusRepository.findById(verifiedReadStatusId).isEmpty(),
					"ID로 ReadStatus 삭제");
		} finally {
			if (createdReadStatusId != null) {
				readStatusRepository.deleteById(createdReadStatusId);
			}
		}
	}

	static void messageCreateTest(
			MessageService messageService,
			Channel channel,
			UserDto author
	) {
		Message message = messageService.create(
				new MessageCreateRequest("안녕하세요.", channel.getId(), author.id(), null)
		);
            System.out.println("[완료] 메시지 생성: id=" + message.getId()
                    + ", channelId=" + channel.getId()
                    + ", authorId=" + author.id());
	}

	/** 첨부파일 생성, 채널별 조회, 수정, 삭제의 동작을 단계별로 확인합니다. */
	private static void verifyMessageService(
			MessageService messageService,
			UserDto author,
			Channel channel,
			ChannelService channelService,
			BinaryContentRepository binaryContentRepository
	) {
		String runId = UUID.randomUUID().toString().substring(0, 8);
		Channel otherChannel = channelService.createPublic(
				new PublicChannelCreateRequest("메시지검증-" + runId, "채널별 조회 검증")
		);
		UUID attachedMessageId = null;
		UUID plainMessageId = null;

		try {
			startSection("MessageService | 첨부파일 선택 등록");
			Message attachedMessage = messageService.create(new MessageCreateRequest(
					"첨부파일 두 개가 있는 메시지",
					channel.getId(),
					author.id(),
					Arrays.asList(
							new BinaryContentCreateRequest("first.txt", "text/plain", new byte[]{1, 2}),
							new BinaryContentCreateRequest("second.png", "image/png", new byte[]{3, 4})
					)
			));
			attachedMessageId = attachedMessage.getId();
			UUID verifiedAttachedMessageId = attachedMessageId;
            logData("첨부 메시지", "messageId=" + verifiedAttachedMessageId
                    + ", channelId=" + channel.getId()
                    + ", authorId=" + author.id()
                    + ", attachmentIds=" + attachedMessage.getAttachmentIds());
			check(attachedMessage.getAttachmentIds().size() == 2,
					"메시지에 여러 첨부파일 ID 연결");
			check(attachedMessage.getAttachmentIds().stream()
					.allMatch(id -> binaryContentRepository.findById(id).isPresent()),
					"첨부파일 데이터 저장");

			Message plainMessage = messageService.create(new MessageCreateRequest(
					"첨부파일 없는 메시지",
					otherChannel.getId(),
					author.id(),
					null
			));
			plainMessageId = plainMessage.getId();
			UUID verifiedPlainMessageId = plainMessageId;
            logData("일반 메시지", "messageId=" + verifiedPlainMessageId
                    + ", channelId=" + otherChannel.getId()
                    + ", authorId=" + author.id()
                    + ", attachmentCount=" + plainMessage.getAttachmentIds().size());
			check(plainMessage.getAttachmentIds().isEmpty(), "첨부파일 없이 메시지 생성");

			startSection("MessageService | 채널별 조회");
			List<Message> selectedChannelMessages =
					messageService.findAllByChannelId(channel.getId());
            logData("조회 조건 및 결과", "channelId=" + channel.getId()
                    + ", returnedMessageIds=" + selectedChannelMessages.stream()
                    .map(Message::getId).toList());
			check(selectedChannelMessages.stream()
					.anyMatch(message -> message.getId().equals(verifiedAttachedMessageId)),
					"지정 채널의 메시지 조회");
			check(selectedChannelMessages.stream()
					.noneMatch(message -> message.getId().equals(verifiedPlainMessageId)),
					"다른 채널의 메시지는 조회 결과에서 제외");

			startSection("MessageService | 메시지 수정");
			messageService.update(new MessageUpdateRequest(attachedMessageId, "수정된 메시지 내용"));
			logData("메시지 수정 결과", "messageId=" + attachedMessageId
					+ ", content=" + messageService.findById(attachedMessageId).getContent());
			check(messageService.findById(attachedMessageId).getContent().equals("수정된 메시지 내용"),
					"요청 DTO의 내용으로 메시지 수정");

			startSection("MessageService | 메시지와 첨부파일 삭제");
            logData("삭제 대상 메시지와 첨부파일", "messageId=" + attachedMessageId
                    + ", attachmentIds=" + attachedMessage.getAttachmentIds());
			List<UUID> attachmentIds = attachedMessage.getAttachmentIds();
			messageService.delete(attachedMessageId);
			attachedMessageId = null;
			check(attachmentIds.stream()
					.allMatch(id -> binaryContentRepository.findById(id).isEmpty()),
					"메시지 삭제 시 연결된 첨부파일도 삭제");
			check(messageService.findAllByChannelId(channel.getId()).stream()
					.noneMatch(message -> message.getId().equals(attachedMessage.getId())),
					"삭제된 메시지는 채널 조회에서 제외");
			messageService.delete(plainMessageId);
			plainMessageId = null;
		} finally {
			if (attachedMessageId != null) {
				cleanupMessage(messageService, attachedMessageId);
			}
			if (plainMessageId != null) {
				cleanupMessage(messageService, plainMessageId);
			}
			channelService.delete(otherChannel.getId());
		}
	}

	private static void cleanupMessage(MessageService messageService, UUID messageId) {
		try {
			messageService.delete(messageId);
		} catch (IllegalArgumentException ignored) {
			// 검증 도중 이미 삭제된 메시지는 다시 정리할 필요가 없습니다.
		}
	}

	/** 채널 생성, 조회, 수정, 삭제의 고도화 요구사항을 기능별로 확인합니다. */
	private static void verifyChannelService(
			ChannelService channelService,
			UserService userService,
			UserDto firstUser,
			ChannelRepository channelRepository,
			MessageRepository messageRepository,
			ReadStatusRepository readStatusRepository,
			BinaryContentRepository binaryContentRepository
	) {
		String runId = UUID.randomUUID().toString().substring(0, 8);
		UserDto secondUser = setupVerificationUser(userService, runId, "second");
		UserDto outsider = setupVerificationUser(userService, runId, "outsider");
		UUID publicChannelId = null;
		UUID privateChannelId = null;
		UUID latestMessageId = null;
		UUID latestMessageAttachmentId = null;

		try {
			startSection("ChannelService | 채널 생성");
			Channel publicChannel = channelService.createPublic(
					new PublicChannelCreateRequest("공개-" + runId, "공개 채널 검증")
			);
			publicChannelId = publicChannel.getId();
			UUID verifiedPublicChannelId = publicChannelId;
			logData("정상 PUBLIC 채널 생성", "channelId=" + publicChannelId
					+ ", name=" + publicChannel.getName()
					+ ", description=" + publicChannel.getDescription());

			startSection("ChannelService | PRIVATE 참여자 검증");
			int channelCountBeforeInvalidRequest = channelRepository.findAll().size();
			int readStatusCountBeforeInvalidRequest = readStatusRepository.findAll().size();
			List<UUID> nullParticipantIds = Arrays.asList(firstUser.id(), null);
			logData("잘못된 PRIVATE 요청 입력", "participantUserIds=" + nullParticipantIds);
			expectIllegalArgument(
					() -> channelService.createPrivate(
							new PrivateChannelCreateRequest(nullParticipantIds)),
					"null 참여자 ID가 포함된 PRIVATE 채널 생성 거부"
			);
			int channelCountAfterNullParticipant = channelRepository.findAll().size();
			int readStatusCountAfterNullParticipant = readStatusRepository.findAll().size();
			check(channelCountAfterNullParticipant == channelCountBeforeInvalidRequest
					&& readStatusCountAfterNullParticipant == readStatusCountBeforeInvalidRequest,
					"null 참여자 요청은 채널과 ReadStatus를 저장하지 않음");
			System.out.printf(
					"저장 결과: channels %d -> %d, readStatuses %d -> %d%n",
					channelCountBeforeInvalidRequest, channelCountAfterNullParticipant,
					readStatusCountBeforeInvalidRequest, readStatusCountAfterNullParticipant
			);

			UUID missingParticipantId = UUID.randomUUID();
			List<UUID> missingUserParticipantIds =
					Arrays.asList(firstUser.id(), missingParticipantId);
			logData("잘못된 PRIVATE 요청 입력", "participantUserIds=" + missingUserParticipantIds);
			expectIllegalArgument(
					() -> channelService.createPrivate(
							new PrivateChannelCreateRequest(missingUserParticipantIds)),
					"존재하지 않는 사용자 ID가 포함된 PRIVATE 채널 생성 거부"
			);
			int channelCountAfterMissingUser = channelRepository.findAll().size();
			int readStatusCountAfterMissingUser = readStatusRepository.findAll().size();
			check(channelCountAfterMissingUser == channelCountBeforeInvalidRequest
					&& readStatusCountAfterMissingUser == readStatusCountBeforeInvalidRequest,
					"존재하지 않는 사용자 요청은 채널과 ReadStatus를 저장하지 않음");
			System.out.printf(
					"저장 결과: channels %d -> %d, readStatuses %d -> %d%n",
					channelCountBeforeInvalidRequest, channelCountAfterMissingUser,
					readStatusCountBeforeInvalidRequest, readStatusCountAfterMissingUser
			);

			Channel privateChannel = channelService.createPrivate(
					new PrivateChannelCreateRequest(
							Arrays.asList(firstUser.id(), secondUser.id())
					)
			);
			privateChannelId = privateChannel.getId();
			UUID verifiedPrivateChannelId = privateChannelId;
			logData("정상 PRIVATE 채널 생성", "channelId=" + privateChannelId
					+ ", participantUserIds=" + Arrays.asList(firstUser.id(), secondUser.id()));
			check(publicChannel.getType() == ChannelType.PUBLIC, "PUBLIC 채널 생성");
			check(privateChannel.getType() == ChannelType.PRIVATE
					&& privateChannel.getName() == null
					&& privateChannel.getDescription() == null,
					"PRIVATE 채널은 이름과 설명 없이 생성");
			check(readStatusRepository.findByUserIdAndChannelId(firstUser.id(), privateChannelId).isPresent()
					&& readStatusRepository.findByUserIdAndChannelId(secondUser.id(), privateChannelId).isPresent(),
					"PRIVATE 채널 참여자별 ReadStatus 생성");

			startSection("ChannelService | 채널 조회 DTO");
			BinaryContent messageAttachment = new BinaryContent(
					"channel-delete-check.txt", "text/plain", new byte[]{1, 2, 3}
			);
			binaryContentRepository.save(messageAttachment);
			latestMessageAttachmentId = messageAttachment.getId();
			Message latestMessage = new Message(
					"최근 메시지 확인", firstUser.id(), publicChannelId,
					List.of(latestMessageAttachmentId)
			);
			messageRepository.save(latestMessage);
			latestMessageId = latestMessage.getId();
			logData("삭제 검증용 메시지", "messageId=" + latestMessageId
					+ ", channelId=" + publicChannelId
					+ ", attachmentId=" + latestMessageAttachmentId);
			var publicDto = channelService.findById(publicChannelId);
			var privateDto = channelService.findById(privateChannelId);
			logData("채널 조회 결과", "public.lastMessageAt=" + publicDto.lastMessageAt()
					+ ", private.participantUserIds=" + privateDto.participantUserIds());
			check(latestMessage.getCreatedAt().equals(publicDto.lastMessageAt()),
					"채널 DTO에 가장 최근 메시지 시각 포함");
			var publicListDto = channelService.findAllByUserId(firstUser.id()).stream()
					.filter(item -> item.id().equals(verifiedPublicChannelId))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("공개 채널이 사용자 목록에 없습니다."));
			check(latestMessage.getCreatedAt().equals(publicListDto.lastMessageAt()),
					"사용자별 채널 목록 DTO에도 최근 메시지 시각 포함");
			check(privateDto.participantUserIds().containsAll(
					Arrays.asList(firstUser.id(), secondUser.id())),
					"PRIVATE 채널 DTO에 참여자 ID 포함");
			var privateListDto = channelService.findAllByUserId(firstUser.id()).stream()
					.filter(item -> item.id().equals(verifiedPrivateChannelId))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("비공개 채널이 참여자 목록에 없습니다."));
			check(privateListDto.participantUserIds().containsAll(
					Arrays.asList(firstUser.id(), secondUser.id())),
					"사용자별 채널 목록 DTO에도 PRIVATE 참여자 ID 포함");

			startSection("ChannelService | 사용자별 채널 목록");
			var firstUserChannels = channelService.findAllByUserId(firstUser.id());
			var outsiderChannels = channelService.findAllByUserId(outsider.id());
			logData("참여자 채널 조회", "userId=" + firstUser.id()
					+ ", channelIds=" + firstUserChannels.stream().map(item -> item.id()).toList());
			logData("비참여자 채널 조회", "userId=" + outsider.id()
					+ ", channelIds=" + outsiderChannels.stream().map(item -> item.id()).toList());
			check(firstUserChannels.stream().anyMatch(item -> item.id().equals(verifiedPublicChannelId))
					&& outsiderChannels.stream().anyMatch(item -> item.id().equals(verifiedPublicChannelId)),
					"PUBLIC 채널은 모든 사용자 목록에 포함");
			check(firstUserChannels.stream().anyMatch(item -> item.id().equals(verifiedPrivateChannelId))
					&& outsiderChannels.stream().noneMatch(item -> item.id().equals(verifiedPrivateChannelId)),
					"PRIVATE 채널은 참여자에게만 노출");

			startSection("ChannelService | 수정 및 삭제");
			channelService.update(new ChannelUpdateRequest(
					verifiedPublicChannelId, "수정된 공개-" + runId, "공개 채널 수정 검증"
			));
			check(channelService.findById(verifiedPublicChannelId).name().equals("수정된 공개-" + runId),
					"PUBLIC 채널 수정");
			expectIllegalArgument(
					() -> channelService.update(new ChannelUpdateRequest(
							verifiedPrivateChannelId, "수정 시도", "수정 불가 확인")),
					"PRIVATE 채널 수정 거부"
			);
			logData("삭제 대상 PRIVATE 채널", privateChannel.getId());
			channelService.delete(privateChannelId);
			privateChannelId = null;
			check(channelRepository.findById(privateChannel.getId()).isEmpty(), "채널 삭제");
			check(readStatusRepository.findByUserIdAndChannelId(firstUser.id(), privateChannel.getId()).isEmpty()
					&& readStatusRepository.findByUserIdAndChannelId(secondUser.id(), privateChannel.getId()).isEmpty(),
					"채널 삭제 시 관련 ReadStatus도 삭제");

			logData("PUBLIC 채널 삭제 전 저장 상태",
					"channelExists=" + channelRepository.findById(publicChannelId).isPresent()
					+ ", messageExists=" + messageRepository.findById(latestMessageId).isPresent()
					+ ", attachmentExists=" + binaryContentRepository.findById(latestMessageAttachmentId).isPresent());
			channelService.delete(publicChannelId);
			publicChannelId = null;
			boolean channelStillExists = channelRepository.findById(publicChannel.getId()).isPresent();
			boolean messageStillExists = messageRepository.findById(latestMessage.getId()).isPresent();
			boolean attachmentStillExists =
					binaryContentRepository.findById(latestMessageAttachmentId).isPresent();
			logData("PUBLIC 채널 삭제 후 저장 상태",
					"channelExists=" + channelStillExists
					+ ", messageExists=" + messageStillExists
					+ ", attachmentExists=" + attachmentStillExists);
			check(!channelStillExists && !messageStillExists,
					"채널 삭제 시 채널과 관련 메시지 삭제");
			check(!attachmentStillExists,
					"채널 삭제 시 메시지 첨부 BinaryContent도 삭제");
		} finally {
			if (privateChannelId != null) {
				cleanupChannel(channelService, privateChannelId);
			}
			if (publicChannelId != null) {
				cleanupChannel(channelService, publicChannelId);
			}
			if (latestMessageId != null) {
				messageRepository.deleteById(latestMessageId);
			}
			if (latestMessageAttachmentId != null) {
				binaryContentRepository.deleteById(latestMessageAttachmentId);
			}
			cleanupUser(userService, secondUser.id());
			cleanupUser(userService, outsider.id());
		}
	}

	private static UserDto setupVerificationUser(UserService userService, String runId, String suffix) {
		return userService.create(new UserCreateRequest(
				"channel-check-" + suffix + "-" + runId,
				"channel-check-" + suffix + "-" + runId + "@codeit.com",
				"password",
				null
		));
	}

	private static void cleanupChannel(ChannelService channelService, UUID channelId) {
		try {
			channelService.delete(channelId);
		} catch (IllegalArgumentException ignored) {
			// 검증 도중 이미 삭제된 채널은 다시 정리할 필요가 없습니다.
		}
	}

	private static void verifyUserService(
			UserService userService,
			BinaryContentRepository binaryContentRepository,
			UserStatusRepository userStatusRepository
	) {
		String runId = UUID.randomUUID().toString().substring(0, 8);
		String username = "dto-check-" + runId;
		String email = username + "@codeit.com";
		UUID profileUserId = null;
		UUID noProfileUserId = null;

		try {
			startSection("UserService | 사용자 생성과 상태");
			BinaryContentCreateRequest initialImage = new BinaryContentCreateRequest(
					"profile.png",
					"image/png",
					new byte[]{1, 2, 3}
			);
			UserDto profileUser = userService.create(new UserCreateRequest(
					username, email, "password-1", initialImage
			));
			profileUserId = profileUser.id();

			UserDto noProfileUser = userService.create(new UserCreateRequest(
					username + "-second",
					runId + "-second@codeit.com",
					"password-2",
					null
			));
			noProfileUserId = noProfileUser.id();
            logData("프로필 사용자를 생성", "userId=" + profileUser.id()
                    + ", username=" + profileUser.username()
                    + ", profileId=" + profileUser.profileId());
            logData("프로필 없는 사용자를 생성", "userId=" + noProfileUser.id()
                    + ", username=" + noProfileUser.username()
                    + ", profileId=" + noProfileUser.profileId());

			check(profileUser.profileId() != null, "생성 시 프로필 이미지를 선택적으로 등록");
			check(binaryContentRepository.findById(profileUser.profileId()).isPresent(),
					"프로필 이미지가 저장소에 저장됨");
			check(noProfileUser.profileId() == null, "프로필 이미지 없이 사용자 생성");
			check(profileUser.online(), "사용자 생성 시 온라인 상태 포함");
			check(userStatusRepository.findByUserId(profileUser.id()).isPresent(),
					"사용자 생성 시 UserStatus 생성");

			startSection("UserService | 조회 DTO");
			UserDto foundUser = userService.findById(profileUser.id());
			check(foundUser.id().equals(profileUser.id()) && foundUser.online(),
					"findById가 온라인 상태를 포함한 DTO를 반환");
			check(userService.findAll().stream()
					.anyMatch(user -> user.id().equals(noProfileUser.id()) && user.online()),
					"findAll이 온라인 상태를 포함한 사용자 DTO 목록을 반환");
			check(Arrays.stream(UserDto.class.getRecordComponents())
					.noneMatch(component -> component.getName().equals("password")),
					"UserDto에서 비밀번호 제외");

			startSection("UserService | 중복 username/email 거부");
            logData("중복 검사 기준", "username=" + username + ", email=" + email);
			logData("username 중복 생성 입력", "username=" + username
					+ ", email=" + runId + "-different@codeit.com");
			expectIllegalArgument(
					() -> userService.create(new UserCreateRequest(
							username, runId + "-different@codeit.com", "password", null)),
					"중복 username 생성 거부"
			);
			logData("email 중복 생성 입력", "username=" + username + "-different"
					+ ", email=" + email);
			expectIllegalArgument(
					() -> userService.create(new UserCreateRequest(
							username + "-different", email, "password", null)),
					"중복 email 생성 거부"
			);
			logData("username 중복 수정 입력", "targetUserId=" + noProfileUser.id()
					+ ", username=" + username + ", email=" + runId + "-update@codeit.com");
			expectIllegalArgument(
					() -> userService.update(new UserUpdateRequest(
							noProfileUser.id(), username, runId + "-update@codeit.com",
							"password", null)),
					"중복 username 수정 거부"
			);
			logData("email 중복 수정 입력", "targetUserId=" + noProfileUser.id()
					+ ", username=" + username + "-update" + ", email=" + email);
			expectIllegalArgument(
					() -> userService.update(new UserUpdateRequest(
							noProfileUser.id(), username + "-update", email,
							"password", null)),
					"중복 email 수정 거부"
			);

			startSection("UserService | 프로필 수정");
			UUID originalProfileId = profileUser.profileId();
			UserDto updatedWithoutImage = userService.update(new UserUpdateRequest(
					profileUser.id(), username + "-updated", email, "password-updated", null
			));
			check(originalProfileId.equals(updatedWithoutImage.profileId()),
					"수정 요청에 이미지가 없으면 기존 프로필 유지");

			BinaryContentCreateRequest replacementImage = new BinaryContentCreateRequest(
					"replacement.png", "image/png", new byte[]{4, 5, 6}
			);
			UserDto updatedWithImage = userService.update(new UserUpdateRequest(
					profileUser.id(), username + "-updated", email,
					"password-updated", replacementImage
			));
			logData("사용자/프로필 수정 결과", "userId=" + updatedWithImage.id()
					+ ", username=" + updatedWithImage.username()
					+ ", previousProfileId=" + originalProfileId
					+ ", newProfileId=" + updatedWithImage.profileId());
			check(updatedWithImage.username().equals(username + "-updated"),
					"사용자 정보 수정");
			check(!originalProfileId.equals(updatedWithImage.profileId()),
					"수정 시 새 프로필 이미지로 교체");
			check(binaryContentRepository.findById(updatedWithImage.profileId()).isPresent(),
					"교체한 프로필 이미지가 저장됨");

			startSection("UserService | 연관 데이터와 사용자 삭제");
			UUID replacedProfileId = updatedWithImage.profileId();
			logData("삭제 대상 사용자와 연관 데이터", "userId=" + profileUser.id()
					+ ", profileId=" + replacedProfileId);
			userService.delete(profileUser.id());
			profileUserId = null;
			check(binaryContentRepository.findById(replacedProfileId).isEmpty(),
					"사용자 삭제 시 프로필 이미지도 삭제");
			check(userStatusRepository.findByUserId(updatedWithImage.id()).isEmpty(),
					"사용자 삭제 시 UserStatus도 삭제");
			expectIllegalArgument(
					() -> userService.findById(updatedWithImage.id()),
					"삭제한 사용자 조회 실패"
			);

			userService.delete(noProfileUser.id());
			noProfileUserId = null;
			check(userStatusRepository.findByUserId(noProfileUser.id()).isEmpty(),
					"프로필이 없는 사용자 삭제 시 UserStatus도 삭제");
		} finally {
			cleanupUser(userService, profileUserId);
			cleanupUser(userService, noProfileUserId);
		}
	}

	private static void verifyAuthService(AuthService authService, UserDto user) {
		startSection("AuthService | 로그인 성공과 실패");
		logData("정상 로그인 요청", "username=" + user.username() + ", password=[마스킹]");
		AuthUserDto loggedInUser = authService.login(
				new AuthLoginRequest(user.username(), "woody1234")
		);

		check(
				loggedInUser.id().equals(user.id()),
				"올바른 아이디와 비밀번호로 로그인"
		);

		logData("존재하지 않는 아이디 로그인 요청", "username=없는사용자, password=[마스킹]");
		expectIllegalArgument(
				() -> authService.login(
						new AuthLoginRequest("없는사용자", "woody1234")
				),
				"존재하지 않는 아이디로 로그인 실패"
		);

		logData("비밀번호 불일치 요청", "username=" + user.username() + ", password=[마스킹]");
		expectIllegalArgument(
				() -> authService.login(
						new AuthLoginRequest(user.username(), "wrong-password")
				),
				"잘못된 비밀번호로 로그인 실패"
		);
	}

	private static void check(boolean condition, String description) {
		if (!condition) {
			System.out.println("  [실패] " + description);
			throw new IllegalStateException("검증 실패: " + description);
		}
		System.out.println("  [통과] " + description);
	}

	private static void expectIllegalArgument(Runnable action, String description) {
		try {
			action.run();
		} catch (IllegalArgumentException expected) {
			System.out.println("  [예상한 예외 발생] " + description);
			System.out.println("    실제 예외: " + expected.getClass().getSimpleName()
					+ " - " + expected.getMessage());
			System.out.println("    처리: 요청이 정상 결과 대신 예외로 거부되었습니다.");
			return;
		}
		System.out.println("  [실패] " + description + " (예외가 발생하지 않음)");
		throw new IllegalStateException("검증 실패: " + description + "에서 예외가 발생하지 않았습니다.");
	}

	private static void startSection(String title) {
		System.out.println();
		System.out.println("=".repeat(80));
		System.out.println("▶ " + title);
		System.out.println("-".repeat(80));
	}

	private static void logData(String label, Object value) {
		System.out.println("  확인 데이터 | " + label + ": " + value);
	}

	private static void cleanupUser(UserService userService, UUID userId) {
		if (userId == null) {
			return;
		}
		try {
			userService.delete(userId);
		} catch (IllegalArgumentException ignored) {
			// 검증 과정에서 이미 삭제된 사용자는 정리할 필요가 없습니다.
		}
	}
}
