package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		AuthService authService = context.getBean(AuthService.class);

		String testId = UUID.randomUUID().toString();

		//User 테스트
		System.out.println("===== User 테스트 =====");

		BinaryContentRequest profile = new BinaryContentRequest(
				"profile.png","image/png",new byte[]{1,2,3});

		User user = userService.create(
				new UserCreateRequest("테스트-"+testId,"1234",testId+"@example.com"),profile);

		System.out.println(userService.findById(user.getId()));
		System.out.println(binaryContentService.findById(user.getProfileId()).getFileName());

		userService.update(
				new UserUpdateRequest(user.getId(),"수정-"+testId,"5678",testId+"@example.com"),null);

		//로그인 테스트
		System.out.println("----로그인-------");
		System.out.println(authService.login(new LoginRequest("수정-"+testId,"5678")));

		//Channel 테스트
		System.out.println("===== Channel 테스트 =====");

		Channel publicChannel = channelService.createPublic(
				new PublicChannelRequest("공개-"+testId,"공개 채널입니다"));

		Channel privateChannel = channelService.createPrivate(
				new PrivateChannelRequest(List.of(user.getId())));

		channelService.update(
				new ChannelUpdateRequest(publicChannel.getId(),"수정채널-"+testId,"수정한 설명"));

		System.out.println(channelService.findAllByUserId(user.getId()));
		System.out.println(readStatusService.findAllByUserId(user.getId()));

		//Message 테스트
		System.out.println("===== Message 테스트 =====");

		BinaryContentRequest attachment = new BinaryContentRequest(
				"test.txt","text/plain",new byte[]{4,5,6});

		Message message = messageService.create(
				new MessageCreateRequest("안녕하세요",user.getId(),privateChannel.getId()),
				List.of(attachment));

		messageService.update(new MessageUpdateRequest(message.getId(),"수정된 메시지"));

		System.out.println(messageService.findById(message.getId()).getComment());
		System.out.println(messageService.findAllByChannelId(privateChannel.getId()).size());

		//첨부파일이랑 최근 메시지 시간 확인
		System.out.println("----첨부파일-------");
		System.out.println(binaryContentService.findAllByIdIn(message.getAttachmentIds()).size());
		System.out.println(channelService.findById(privateChannel.getId()));

		//채널 지우면 메시지랑 첨부파일 조회상태도 지워지는지
		System.out.println("===== 관계 삭제 테스트 =====");

		channelService.delete(privateChannel.getId());

		System.out.println(messageService.findAllByChannelId(privateChannel.getId()).size());
		System.out.println(binaryContentService.findAllByIdIn(message.getAttachmentIds()).size());
		System.out.println(readStatusService.findAllByUserId(user.getId()).size());

		//예외 테스트
		System.out.println("////////////////////////////////////");

		//이름 중복 테스트
		try{
			userService.create(
					new UserCreateRequest("수정-"+testId,"1234",testId+"-other@example.com"),null);
			System.out.println("이름 중복인데 생성됨");
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		//이메일 중복 테스트
		try{
			userService.create(
					new UserCreateRequest("다른이름-"+testId,"1234",testId+"@example.com"),null);
			System.out.println("이메일 중복인데 생성됨");
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		System.out.println("==============================");

		//비밀번호 틀리게 로그인
		try{
			authService.login(new LoginRequest("수정-"+testId,"틀린비밀번호"));
			System.out.println("비밀번호 틀렸는데 로그인됨");
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		//채널명 중복 테스트
		try{
			channelService.createPublic(
					new PublicChannelRequest("수정채널-"+testId,"중복 테스트"));
			System.out.println("채널명 중복인데 생성됨");
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		System.out.println("==============================");

		Channel testChannel = channelService.createPrivate(
				new PrivateChannelRequest(List.of(user.getId())));

		//PRIVATE 수정 테스트
		try{
			channelService.update(
					new ChannelUpdateRequest(testChannel.getId(),"수정 시도","수정 시도"));
			System.out.println("PRIVATE인데 수정됨");
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		//조회상태 중복 테스트
		try{
			readStatusService.create(new ReadStatusCreateRequest(
					user.getId(),testChannel.getId(),Instant.now()));
			System.out.println("조회상태 중복인데 생성됨");
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}

		//테스트하고 남은거 삭제
		channelService.delete(testChannel.getId());
		channelService.delete(publicChannel.getId());
		userService.delete(user.getId());

		//JCF 테스트
		System.out.println("===== JCF 테스트 =====");

		ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();
		UserStatusRepository userStatusRepository = new JCFUserStatusRepository();
		BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();

		ReadStatus readStatus = new ReadStatus(UUID.randomUUID(),UUID.randomUUID(),Instant.now());
		UserStatus userStatus = new UserStatus(UUID.randomUUID(),Instant.now());
		BinaryContent binaryContent = new BinaryContent("test.txt","text/plain",new byte[]{1,2,3});

		//저장
		readStatusRepository.save(readStatus);
		userStatusRepository.save(userStatus);
		binaryContentRepository.save(binaryContent);

		//조회
		System.out.println(readStatusRepository.findById(readStatus.getId()).isPresent());
		System.out.println(userStatusRepository.findById(userStatus.getId()).isPresent());
		System.out.println(binaryContentRepository.findById(binaryContent.getId()).isPresent());

		//삭제
		readStatusRepository.deleteById(readStatus.getId());
		userStatusRepository.deleteById(userStatus.getId());
		binaryContentRepository.deleteById(binaryContent.getId());

		System.out.println(readStatusRepository.findAll().size());
		System.out.println(userStatusRepository.findAll().size());
		System.out.println(binaryContentRepository.findAll().size());

		System.out.println("===== 테스트 끝 =====");
	}
}