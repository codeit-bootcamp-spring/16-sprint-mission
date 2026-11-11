package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {

	private final UserService userService;
	private final ChannelService channelService;
	private final MessageService messageService;

	public DiscodeitApplication(UserService userService, ChannelService channelService, MessageService messageService) {
		this.userService = userService;
		this.channelService = channelService;
		this.messageService = messageService;
	}

	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("테스트 시작");
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);

		messageCreateTest(messageService, channel, user);
		System.out.println("테스트 끝");
	}

	/*static User setupUser(UserService userService) {
		return userService.create("woody", "woody@codeit.com", "woody1234");
	}*/
	static User setupUser(UserService userService) {
		// UserCreateDto (프로필 이미지가 없을 경우 뒤 필드들은 null 또는 0L 처리)
		UserCreateDto dto = new UserCreateDto(
				"woody",
				"woody@codeit.com",
				"woody1234",
				null, null, 0L, null
		);
		return userService.create(dto);
	}

/*
	static Channel setupChannel(ChannelService channelService) {
		return channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
	}*/
static Channel setupChannel(ChannelService channelService) {
	PublicChannelCreateDto dto = new PublicChannelCreateDto("공지", "공지 채널입니다.");
	return channelService.createPublicChannel(dto);
}


/*	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
		System.out.println("메시지 생성: " + message.getId());
	}*/
static void messageCreateTest(MessageService messageService, Channel channel, User author) {
	MessageCreateDto dto = new MessageCreateDto(
			"안녕하세요.",
			channel.getId(),
			author.getId(),
			Collections.emptyList()
	);
	Message message = messageService.create(dto);
	System.out.println("메시지 생성: " + message.getId());
	}

}



