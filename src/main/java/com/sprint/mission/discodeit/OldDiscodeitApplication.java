/*
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class OldDiscodeitApplication {

    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        // SpringApplication.run(DiscodeitApplication.class, args);

        // ConfigurableApplicationContext는 는 스프링 컨테이너의 설정과 생명주기를 관리하는 인터페이스입니다. 빈의 생명주기가 아닙니다.
        // 스프링 애플리케이션을 실행하고, 생성된 ApplicationContext를 반환받습니다.
        // ApplicationContext는 스프링(IoC) 컨테이너입니다.
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 리포지터리 빈 초기화
        // context.getBean()은 스프링 컨테이너에 등록된 모든 빈 중, 원하는 빈을 가져오는 메서드입니다.
        // 인자로 *Repository.class를 전달하는 이유는, 클래스의 인스턴스를 전달하는 것이 아닌
        // 클래스 객체를 전달하는 것 이기 때문입니다. 이는 스프링이 해당 빈의 타입을 조회하기 때문입니다.
        // 스프링 빈은 이런방식으로 가져옵니다.
        UserRepository userRepository = context.getBean(UserRepository.class);
        ChannelRepository channelRepository = context.getBean(ChannelRepository.class);
        MessageRepository messageRepository = context.getBean(MessageRepository.class);

        // 서비스 빈 초기화
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        // 테스트
        messageCreateTest(messageService, channel, user);
    }

}
 */