package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;

@SpringBootApplication
public class DiscodeitApplication {

	static UserResponseDto setupUser(UserService userService) {
		UserResponseDto user = userService.create(
				new UserCreateRequest("woody", "woody@codeit.com", "woody1234", null));
		return user;
	}

	static ChannelResponseDto setupChannel(ChannelService channelService) {
		ChannelResponseDto channel = channelService.createPublic(
				new PublicChannelCreateRequest("공지", "공지 채널입니다."));
		return channel;
	}

	static void messageCreateTest(MessageService messageService, ChannelResponseDto channel, UserResponseDto author) {
		Message message = messageService.create(
				new MessageCreateRequest("안녕하세요.", channel.id(), author.id(), null));
		System.out.println("메시지 생성: " + message.getId());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		UserResponseDto user = setupUser(userService);
		ChannelResponseDto channel = setupChannel(channelService);
		messageCreateTest(messageService, channel, user);
	}
}