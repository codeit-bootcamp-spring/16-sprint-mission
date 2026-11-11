package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.userRequest.UserUpdateRequest;

// JCF, File 구현체 및 Basic 구현체를 호출 및 실행하기 위해 import를 적용했습니다.
import com.sprint.mission.discodeit.dto.request.channelRequest.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.repository.file.repo_file_consoleRun.ReFileChannelConsoleRun;
import com.sprint.mission.discodeit.repository.file.repo_file_consoleRun.ReFileMessageConsoleRun;
import com.sprint.mission.discodeit.repository.file.repo_file_consoleRun.ReFileUserConsoleRun;
import com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun.ReJCFChannelConsoleRun;
import com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun.ReJCFMessageConsoleRun;
import com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun.ReJCFUserConsoleRun;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.basic.basic_serviceConsoleRun.BasicServiceConsoleRun;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
        UserDto user = setupUser(userService);
        System.out.println("\n===== 신규 유저 생성 =====");
        System.out.println("신규 유저 이름 : " + user.username());
        System.out.println("신규 유저 ID : " + user.id() + "\n");

        // 4. 유저 이름 수정 테스트
        user = userUpdateTest(userService, user);

        // 5. 채널 생성 및 정보 출력
        ChannelDto channel = setupChannel(channelService);
        System.out.println("===== 신규 채널 생성 =====");
        System.out.println("신규 채널 이름 : " + channel.channelName());
        System.out.println("신규 채널 ID : " + channel.id());

        // 6. 유저의 채널 참여 및 참여 여부 확인
        channelService.joinChannel(channel.id(), user.id());
        System.out.println(user.username() + " 유저, " + channel.channelName() + " 채널 참여 완료" + "\n");
        System.out.println("===== 채널 참여 테스트 =====");
        try {
            boolean joined = channelService.isMember(channel.id(), user.id());

            System.out.println("유저 이름 : " + user.username());
            System.out.println("채널 이름 : " + channel.channelName());
            System.out.println(user.username() + "님, 참여 여부 : " + (joined ? "참여 중" : "미참여") + "\n");
        } catch (IllegalArgumentException iaException){
            // 참여 여부 확인 중 발생한 예외를 처리한다.
            System.out.println("채널 참여 여부 확인 실패: " + iaException.getMessage());
        }

        // 7. 메시지 생성 테스트
        messageCreateTest(messageService, channel, user);

        // 8. Repository CRUD 테스트
//        printJCFRepositoryCRUDInformation();
        printFILERepositoryCRUDInformation();
//        BasicServiceConsoleRun.basicServiceTest();
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
    static UserDto setupUser(UserService userService){
        UserCreateRequest request = new UserCreateRequest(
                "요네즈 켄시",
                "kenshi@example.com",
                "password123",
                null
        );
        return userService.createUser(request);
    }

    // 유저 이름 수정 테스트
    static UserDto userUpdateTest(UserService userService, UserDto source){
        // 기존 유저의 이름을 새로운 이름으로 변경한다.
        // 수정 요청 DTO 생성
        UserUpdateRequest request = new UserUpdateRequest(
                source.id(),
                "후지이 카제",
                source.email(),
                "Password1q2w3e4r",
                null
        );

        // 수정된 유저를 서비스에 저장한다.
        UserDto updatedUser = userService.updateUser(request);

        System.out.println("===== 유저 이름 수정 =====");
        System.out.println("변경 전 이름 : " + source.username());
        System.out.println("변경 후 이름 : " + updatedUser.username());
        System.out.println("유저 ID : " + updatedUser.id() + "\n");

        return updatedUser;
    }

    // 채널 생성
    static ChannelDto setupChannel(ChannelService channelService){
        return channelService.createPublicChannel(
                new PublicChannelCreateRequest("음악", "음악 채널"));
    }

    // 메시지 생성 테스트
    static void messageCreateTest(
            MessageService messageService, ChannelDto channel, UserDto author){

        MessageDto message = messageService.createMessage(new MessageCreateRequest(
                "곤니찌와~ 요로시쿠 오네가이시 마스!",
                author.id(), channel.id(),null));

        System.out.println("===== 메시지 생성 =====");
        System.out.println("작성자 : " + author.username());
        System.out.println("메시지 내용 : " + message.content());
        System.out.println("메시지 ID : " + message.id() + "\n");
    }
}