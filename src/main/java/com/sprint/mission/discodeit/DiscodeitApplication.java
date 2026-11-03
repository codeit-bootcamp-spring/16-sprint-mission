
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.time.Instant;
import java.util.List;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.BinaryContent;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				SpringApplication.run(DiscodeitApplication.class, args);

		System.out.println("ReadStatusService Bean: "
				+ context.getBean(ReadStatusService.class).getClass().getSimpleName());

		System.out.println("UserStatusService Bean: "
				+ context.getBean(UserStatusService.class).getClass().getSimpleName());

		System.out.println("BinaryContentService Bean: "
				+ context.getBean(BinaryContentService.class).getClass().getSimpleName());


		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		System.out.println("사용자 수: " + userService.getAll().size());
		System.out.println("채널 수: " + channelService.getAll().size());
		System.out.println("메시지 수: " + messageService.getAll().size());
		User user = userService.add(new User("Spring 사용자"));

		Channel channel = channelService.add(new Channel("Spring 채널"));

		Message message = messageService.add(
				new Message(
						"Spring 메시지 테스트", user.getId(), channel.getId()
				)
		);


		ReadStatusService readStatusService =
				context.getBean(ReadStatusService.class);

		ReadStatus readStatus = readStatusService.create(
				new ReadStatusCreateRequest(
						user.getId(), channel.getId(), Instant.now()
				)
		);

		System.out.println("ReadStatus 생성 및 조회: "
				+ (readStatusService.find(readStatus.getId()) != null));

		System.out.println("ReadStatus 사용자별 조회: "
				+ !readStatusService.findAllByUserId(user.getId()).isEmpty());

		Instant newReadTime = Instant.now();

		ReadStatus updatedReadStatus = readStatusService.update(
				new ReadStatusUpdateRequest(readStatus.getId(), newReadTime)
		);

		System.out.println("ReadStatus 수정: "
				+ updatedReadStatus.getLastReadAt().equals(newReadTime));

		readStatusService.delete(readStatus.getId());

		System.out.println("ReadStatus 삭제: "
				+ (readStatusService.find(readStatus.getId()) == null));

		UserStatusService userStatusService =
				context.getBean(UserStatusService.class);

		UserStatus userStatus = userStatusService.create(
				new UserStatusCreateRequest(
						user.getId(),
						Instant.now()
				)
		);

		System.out.println("UserStatus 생성 및 조회: "
				+ (userStatusService.find(userStatus.getId()) != null));

		System.out.println("UserStatus 온라인 상태: "
				+ userStatus.isOnline());

		Instant newActiveTime = Instant.now();

		UserStatus updatedUserStatus = userStatusService.updateByUserId(
				new UserStatusUpdateByUserIdRequest(
						user.getId(),
						newActiveTime
				)
		);

		System.out.println("UserStatus 수정: "
				+ updatedUserStatus.getLastActiveAt().equals(newActiveTime));

		userStatusService.delete(userStatus.getId());

		System.out.println("UserStatus 삭제: "
				+ (userStatusService.find(userStatus.getId()) == null));


		BinaryContentService binaryContentService =
				context.getBean(BinaryContentService.class);

		BinaryContent binaryContent = binaryContentService.create(
				new BinaryContentCreateRequest(
						"test.png",
						"image/png",
						new byte[]{1, 2, 3}
				)
		);

		System.out.println("BinaryContent 생성 및 조회: "
				+ (binaryContentService.find(binaryContent.getId()) != null));

		List<BinaryContent> contents =
				binaryContentService.findAllByIdIn(
						List.of(binaryContent.getId())
				);

		System.out.println("BinaryContent 목록 조회: "
				+ (contents.size() == 1));

		System.out.println("BinaryContent 파일 데이터: "
				+ (binaryContent.getBytes().length == 3));

		binaryContentService.delete(binaryContent.getId());

		System.out.println("BinaryContent 삭제: "
				+ (binaryContentService.find(binaryContent.getId()) == null));


		System.out.println("생성된 사용자: " + user.getUsername());
		System.out.println("생성된 채널: " + channel.getName());
		System.out.println("생성된 메시지: " + message.getContent());
		System.out.println("조회된 사용자: "
				+ userService.getById(user.getId()).getUsername());

		System.out.println("조회된 채널: "
				+ channelService.getById(channel.getId()).getName());

		System.out.println("조회된 메시지: "
				+ messageService.getById(message.getId()).getContent());

		userService.update(user.getId(), "수정된 사용자");
		channelService.update(channel.getId(), "수정된 채널");
		messageService.update(message.getId(), "수정된 메시지");

		System.out.println("수정된 사용자: "
				+ userService.getById(user.getId()).getUsername());

		System.out.println("수정된 채널: "
				+ channelService.getById(channel.getId()).getName());

		System.out.println("수정된 메시지: "
				+ messageService.getById(message.getId()).getContent());

		messageService.delete(message.getId());
		channelService.delete(channel.getId());
		userService.delete(user.getId());

		System.out.println("메시지 삭제 완료");
		System.out.println("채널 삭제 완료");
		System.out.println("사용자 삭제 완료");

		System.out.println("사용자 삭제 확인: "
				+ userService.getAll().stream()
				.noneMatch(u -> u.getId().equals(user.getId())));

		System.out.println("채널 삭제 확인: "
				+ channelService.getAll().stream()
				.noneMatch(c -> c.getId().equals(channel.getId())));

		System.out.println("메시지 삭제 확인: "
				+ messageService.getAll().stream()
				.noneMatch(m -> m.getId().equals(message.getId())));


		String testName = "sprint3-" + UUID.randomUUID().toString().substring(0, 8);

		UserResponse createdUser = userService.create(
				new UserCreateRequest(
						testName,
						testName + "@example.com",
						"dummy-test-password",
						null
				)
		);

		boolean duplicateUsernameRejected = false;

		try {
			userService.create(
					new UserCreateRequest(
							testName,
							testName + "-other@example.com",
							"dummy-test-password",
							null
					)
			);
		} catch (IllegalArgumentException e) {
			duplicateUsernameRejected = true;
		}

		System.out.println("사용자명 중복 검사: "
				+ duplicateUsernameRejected);

		boolean duplicateEmailRejected = false;

		try {
			userService.create(
					new UserCreateRequest(
							testName + "-another",
							testName + "@example.com",
							"dummy-test-password",
							null
					)
			);
		} catch (IllegalArgumentException e) {
			duplicateEmailRejected = true;
		}

		System.out.println("이메일 중복 검사: "
				+ duplicateEmailRejected);

		System.out.println("회원가입 테스트: "
				+ createdUser.username().equals(testName));

		System.out.println("회원 조회 테스트: "
				+ userService.find(createdUser.id()).id().equals(createdUser.id()));

		UserResponse updatedUser = userService.update(
				new UserUpdateRequest(
						createdUser.id(),
						testName + "-updated",
						testName + "-updated@example.com",
						null,
						new BinaryContentCreateRequest(
								"profile.png",
								"image/png",
								new byte[]{1, 2, 3}
						)
				)
		);

		System.out.println("회원정보 수정 테스트: "
				+ updatedUser.username().equals(testName + "-updated"));

		System.out.println("프로필 이미지 등록 테스트: "
				+ (updatedUser.profileImageId() != null));

		System.out.println("온라인 상태 조회 테스트: "
				+ userService.find(createdUser.id()).online());

		UserStatusRepository statusRepository =
				context.getBean(UserStatusRepository.class);

		BinaryContentRepository contentRepository =
				context.getBean(BinaryContentRepository.class);

		userService.delete(createdUser.id());

		System.out.println("회원 삭제 테스트: "
				+ userService.getAll().stream()
				.noneMatch(u -> u.getId().equals(createdUser.id())));

		System.out.println("접속 상태 함께 삭제: "
				+ statusRepository.findByUserId(createdUser.id()).isEmpty());

		System.out.println("프로필 이미지 함께 삭제: "
				+ (contentRepository.getById(updatedUser.profileImageId()) == null));


		ChannelResponse publicChannel = channelService.createPublic(
				new PublicChannelCreateRequest(
						"Sprint3 공개 채널",
						"공개 채널 생성 테스트"
				)
		);

		System.out.println("PUBLIC 채널 생성: "
				+ (publicChannel.type() == ChannelType.PUBLIC));

		System.out.println("PUBLIC 채널 설명 저장: "
				+ "공개 채널 생성 테스트".equals(
				channelService.getById(publicChannel.id()).getDescription()
		));

		ChannelResponse updatedPublicChannel = channelService.update(
				new ChannelUpdateRequest(
						publicChannel.id(),
						"수정된 공개 채널",
						"수정된 채널 설명"
				)
		);

		System.out.println("PUBLIC 채널 이름 수정: "
				+ "수정된 공개 채널".equals(updatedPublicChannel.name()));

		System.out.println("PUBLIC 채널 설명 수정: "
				+ "수정된 채널 설명".equals(
				channelService.getById(publicChannel.id()).getDescription()
		));


		channelService.delete(publicChannel.id());


		Channel privateChannel = channelService.add(
				new Channel(ChannelType.PRIVATE, null, null)
		);

		System.out.println("PRIVATE 채널 타입 확인: "
				+ (channelService.getById(privateChannel.getId()).getType()
				== ChannelType.PRIVATE));

		boolean privateUpdateRejected = false;

		try {
			channelService.update(privateChannel.getId(), "수정 시도");
		} catch (IllegalArgumentException e) {
			privateUpdateRejected = true;
		}

		System.out.println("PRIVATE 채널 수정 금지: "
				+ privateUpdateRejected);

		channelService.delete(privateChannel.getId());


		User member1 = userService.add(new User("PRIVATE 테스트 사용자1"));
		User member2 = userService.add(new User("PRIVATE 테스트 사용자2"));

		ChannelResponse privateResponse = channelService.createPrivate(
				new PrivateChannelCreateRequest(
						List.of(member1.getId(), member2.getId())
				)
		);

		ReadStatusRepository privateReadStatusRepository =
				context.getBean(ReadStatusRepository.class);

		System.out.println("PRIVATE 채널 생성: "
				+ (privateResponse.type() == ChannelType.PRIVATE));

		System.out.println("PRIVATE 참여자 수: "
				+ (privateResponse.participantUserIds().size() == 2));

		System.out.println("PRIVATE ReadStatus 자동 생성: "
				+ (privateReadStatusRepository
				.findAllByChannelId(privateResponse.id()).size() == 2));

		Message privateMessage = messageService.add(
				new Message(
						"PRIVATE 테스트 메시지",
						member1.getId(),
						privateResponse.id()
				)
		);

		MessageResponse foundMessage =
				messageService.find(privateMessage.getId());

		MessageResponse attachedMessage = messageService.create(
				new MessageCreateRequest(
						"첨부파일 테스트 메시지",
						member1.getId(),
						privateResponse.id(),
						List.of(
								new BinaryContentCreateRequest(
										"first.txt",
										"text/plain",
										new byte[]{1, 2, 3}
								),
								new BinaryContentCreateRequest(
										"second.txt",
										"text/plain",
										new byte[]{4, 5, 6}
								)
						)
				)
		);

		System.out.println("첨부파일 2개 저장: "
				+ (attachedMessage.attachmentIds().size() == 2));

		BinaryContentService attachmentService =
				context.getBean(BinaryContentService.class);

		BinaryContent firstAttachment = attachmentService.find(
				attachedMessage.attachmentIds().get(0)
		);

		System.out.println("첨부파일 이름 조회: "
				+ "first.txt".equals(firstAttachment.getFileName()));

		System.out.println("첨부파일 데이터 확인: "
				+ (firstAttachment.getBytes().length == 3));

		System.out.println("메시지 첨부파일 목록 조회: "
				+ (messageService.find(attachedMessage.id())
				.attachmentIds().size() == 2));

		List<UUID> savedAttachmentIds = attachedMessage.attachmentIds();

		messageService.delete(attachedMessage.id());

		System.out.println("첨부 메시지 삭제: "
				+ messageService.getAll().stream()
				.noneMatch(m -> m.getId().equals(attachedMessage.id())));

		System.out.println("첨부파일 함께 삭제: "
				+ attachmentService.findAllByIdIn(savedAttachmentIds).isEmpty());



		System.out.println("메시지 상세 조회: "
				+ foundMessage.id().equals(privateMessage.getId()));

		MessageResponse updatedMessage = messageService.update(
				new MessageUpdateRequest(
						privateMessage.getId(),
						"수정된 PRIVATE 테스트 메시지"
				)
		);

		System.out.println("메시지 DTO 수정: "
				+ "수정된 PRIVATE 테스트 메시지".equals(updatedMessage.content()));

		System.out.println("메시지 수정 후 재조회: "
				+ "수정된 PRIVATE 테스트 메시지".equals(
				messageService.find(privateMessage.getId()).content()
		));

		List<MessageResponse> channelMessages =
				messageService.findAllByChannelId(privateResponse.id());

		System.out.println("채널별 메시지 조회: "
				+ channelMessages.stream()
				.anyMatch(m -> m.id().equals(privateMessage.getId())));

		System.out.println("채널별 조회 결과 검증: "
				+ channelMessages.stream()
				.allMatch(m -> m.channelId().equals(privateResponse.id())));


		ChannelResponse foundPrivate = channelService.find(privateResponse.id());

		System.out.println("PRIVATE 최신 메시지 시간 조회: "
				+ privateMessage.getCreatedAt()
				.equals(foundPrivate.latestMessageCreatedAt()));

		System.out.println("PRIVATE 참여자 목록 조회: "
				+ (foundPrivate.participantUserIds().size() == 2));


		ChannelResponse lookupPublic = channelService.createPublic(
				new PublicChannelCreateRequest(
						"조회 테스트 공개 채널",
						"모든 사용자가 볼 수 있는 채널"
				)
		);

		User outsider = userService.add(
				new User("PRIVATE 미참여 사용자")
		);

		List<ChannelResponse> memberChannels =
				channelService.findAllByUserId(member1.getId());

		List<ChannelResponse> outsiderChannels =
				channelService.findAllByUserId(outsider.getId());

		System.out.println("참여자 PUBLIC 조회: "
				+ memberChannels.stream()
				.anyMatch(c -> c.id().equals(lookupPublic.id())));

		System.out.println("참여자 PRIVATE 조회: "
				+ memberChannels.stream()
				.anyMatch(c -> c.id().equals(privateResponse.id())));

		System.out.println("미참여자 PUBLIC 조회: "
				+ outsiderChannels.stream()
				.anyMatch(c -> c.id().equals(lookupPublic.id())));

		System.out.println("미참여자 PRIVATE 제외: "
				+ outsiderChannels.stream()
				.noneMatch(c -> c.id().equals(privateResponse.id())));

		channelService.delete(lookupPublic.id());
		userService.delete(outsider.getId());

		MessageResponse cascadeMessage = messageService.create(
				new MessageCreateRequest(
						"채널 삭제 첨부파일 테스트",
						member1.getId(),
						privateResponse.id(),
						List.of(
								new BinaryContentCreateRequest(
										"cascade.txt",
										"text/plain",
										new byte[]{1, 2, 3}
								)
						)
				)
		);

		UUID cascadeMessageId = cascadeMessage.id();
		List<UUID> cascadeAttachmentIds = cascadeMessage.attachmentIds();
		channelService.delete(privateResponse.id());
		System.out.println("채널 삭제 확인: "
				+ channelService.getAll().stream()
				.noneMatch(c -> c.getId().equals(privateResponse.id())));

		System.out.println("채널 메시지 삭제 확인: "
				+ messageService.getAll().stream()
				.noneMatch(m -> m.getId().equals(cascadeMessageId)));

		System.out.println("채널 첨부파일 삭제 확인: "
				+ attachmentService.findAllByIdIn(cascadeAttachmentIds).isEmpty());

		System.out.println("PRIVATE 메시지 함께 삭제: "
				+ messageService.getAll().stream()
				.noneMatch(m -> m.getId().equals(privateMessage.getId())));

		System.out.println("PRIVATE ReadStatus 함께 삭제: "
				+ privateReadStatusRepository
				.findAllByChannelId(privateResponse.id()).isEmpty());

		System.out.println("PRIVATE 채널 삭제: "
				+ channelService.getAll().stream()
				.noneMatch(c -> c.getId().equals(privateResponse.id())));

		userService.delete(member1.getId());
		userService.delete(member2.getId());




	}


}
