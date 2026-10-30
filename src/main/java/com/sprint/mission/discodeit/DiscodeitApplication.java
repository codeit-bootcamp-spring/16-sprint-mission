package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

// JCF, File 구현체 및 Basic 구현체를 호출 및 실행하기 위해 import를 적용했습니다.
import com.sprint.mission.discodeit.repository.file.repo_file_consoleRun.ReFileChannelConsoleRun;
import com.sprint.mission.discodeit.repository.file.repo_file_consoleRun.ReFileMessageConsoleRun;
import com.sprint.mission.discodeit.repository.file.repo_file_consoleRun.ReFileUserConsoleRun;
import com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun.ReJCFChannelConsoleRun;
import com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun.ReJCFMessageConsoleRun;
import com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun.ReJCFUserConsoleRun;
import com.sprint.mission.discodeit.service.basic.basic_serviceConsoleRun.BasicServiceConsoleRun;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
        // 1. Spring Context 초기화
		ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        // 2. Spring Context에서 Service Bean 조회
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // userService의 실제 구현체 출력 테스트
        System.out.println(userService.getClass().getSimpleName());

        // 3. 유저 생성 및 정보 출력
        User user = setupUser(userService, null);
        System.out.println("\n===== 신규 유저 생성 =====");
        System.out.println("신규 유저 이름 : " + user.getName());
        System.out.println("신규 유저 ID : " + user.getId() + "\n");

        // 4. 유저 이름 수정 테스트
        user = userUpdateTest(userService, user);

        // 5. 채널 생성 및 정보 출력
        Channel channel = setupChannel(channelService);
        System.out.println("===== 신규 채널 생성 =====");
        System.out.println("신규 채널 이름 : " + channel.getName());
        System.out.println("신규 채널 ID : " + channel.getId());

        // 6. 유저의 채널 참여 및 참여 여부 확인
        channelService.joinChannel(channel.getId(), user.getId());
        System.out.println(user.getName() + " 유저, " + channel.getName() + " 채널 참여 완료" + "\n");
        System.out.println("===== 채널 참여 테스트 =====");
        try {
            boolean joined = channelService.isMember(channel.getId(), user.getId());

            System.out.println("유저 이름 : " + user.getName());
            System.out.println("채널 이름 : " + channel.getName());
            System.out.println(user.getName() + "님, 참여 여부 : " + (joined ? "참여 중" : "미참여") + "\n");
        } catch (IllegalArgumentException iaException){
            // 참여 여부 확인 중 발생한 예외를 처리한다.
            System.out.println("채널 참여 여부 확인 실패: " + iaException.getMessage());
        }

        // 7. 메시지 생성 테스트
        messageCreateTest(messageService, channel, user);

        // 8. Repository CRUD 테스트
//      printJCFRepositoryCRUDInformation();
//      printFILERepositoryCRUDInformation();
//      BasicServiceConsoleRun.basicServiceTest();
	}

    // Repository JCF 구현체 CRUD 테스트 호출문
    public static void printJCFRepositoryCRUDInformation(){
        ReJCFUserConsoleRun.printUserRepositoryInformation();
        ReJCFChannelConsoleRun.printChannelRepositoryInformation();
        ReJCFMessageConsoleRun.printMessageRepositoryInformation();
    }

    // Repository File 구현체 CRUD 테스트 호출문
    public static void printFILERepositoryCRUDInformation(){
        ReFileUserConsoleRun.printFileUserRepositoryInformation();
        ReFileChannelConsoleRun.printFileChannelRepositoryInformation();
        ReFileMessageConsoleRun.printFileMessageRepositoryInformation();
    }

    // 유저 생성
    static User setupUser(UserService userService, UUID profileId){
        User user = new User("요네즈 켄시", profileId);
        return userService.createUser(user);
    }

    // 유저 이름 수정 테스트
    static User userUpdateTest(UserService userService, User source){
        // 기존 유저의 이름을 새로운 이름으로 변경한다.
        User updatedUser = new User(source, "후지이 카제");

        // 수정된 유저를 서비스에 저장한다.
        updatedUser = userService.updateUser(
                updatedUser.getId(), updatedUser.getName());

        System.out.println("===== 유저 이름 수정 =====");
        System.out.println("변경 전 이름 : " + source.getName());
        System.out.println("변경 후 이름 : " + updatedUser.getName());
        System.out.println("유저 ID : " + updatedUser.getId() + "\n");

        return updatedUser;
    }

    // 채널 생성
    static Channel setupChannel(ChannelService channelService){
        Channel channel = new Channel("음악");
        return channelService.createChannel(channel);
    }

    // 메시지 생성 테스트
    static void messageCreateTest(MessageService messageService,
                                  Channel channel, User author){
        Message message = new Message(
                "곤니찌와~ 요로시쿠 오네가이시 마스!", author.getId(), channel.getId(), null);

        message = messageService.createMessage(message);
        System.out.println("===== 메시지 생성 =====");
        System.out.println("작성자 : " + author.getName());
        System.out.println("메시지 내용 : " + message.getContent());
        System.out.println("메시지 ID : " + message.getId() + "\n");
    }
}