package com.sprint.mission.discodeit.service.basic.basic_serviceConsoleRun;

// 엔티티 및 DTO 계층
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.channelRequest.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.userRequest.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFUserStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;

// 레포지터리 계층
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.*;

// 서비스 계층
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Basic*Service 구현체를 활용하여 테스트 해보세요
public class BasicServiceConsoleRun {

    public static void basicServiceTest(){

        // 1. Repository 구현체 초기화한다.
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        // BinaryContentRepository와 UserStatusRepository 구현체도 초기화해야 한다.
        // 실제 JCF 구현체의 패키지 경로에 맞게 import한다.
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();
        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 2. Repository를 주입하여 Basic Service 구현체를 초기화한다.
        UserService userService = new BasicUserService(
                userRepository, binaryContentRepository, userStatusRepository, passwordEncoder);

        ChannelService channelService = new BasicChannelService(
                channelRepository, userRepository, messageRepository, readStatusRepository);

        MessageService messageService = new BasicMessageService(
                binaryContentRepository, messageRepository, userService, channelService);

        // 3. 유저를 생성하고 정보를 출력한다.
        UserDto user = setupUser(userService);
        System.out.println("\n===== 신규 유저 생성 =====");
        System.out.println("신규 유저 이름 : " + user.username());
        System.out.println("신규 유저 ID : " + user.id() + "\n");

        // 3-1. 유저 이름 수정 테스트
        user = userUpdateTest(userService, user);

        // 4. 채널을 생성하고 정보를 출력한다.
        ChannelDto channel = setupChannel(channelService);
        System.out.println("===== 신규 채널 생성 =====");
        System.out.println("신규 채널 이름 : " + channel.channelName());
        System.out.println("신규 채널 ID : " + channel.id());

        // 5. 유저를 채널에 참여시킨다.
        channelService.joinChannel(channel.id(), user.id());
        System.out.println(user.username() + " 유저, " + channel.channelName() + " 채널 참여 완료" + "\n");

        // 5-1. 유저의 채널 참여 여부를 확인한다.
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

        // 6. 메시지 생성 테스트
        messageCreateTest(messageService, channel, user);

        // 7. 변경된 유저 이름, 채널 출력 테스트
        System.out.println("변경된 유저 ID : " + user.id() + ", 이름: " + user.username());
        System.out.println("생성된 채널 ID : " + channel.id() + ", 이름: " + channel.channelName() + " 채널");
    }

    // 유저 생성
    static UserDto setupUser(UserService userService){
        return userService.createUser(new UserCreateRequest(
                "요네즈 켄시",
                "yonezu@test.com",
                "Password1q2w3e4r",
                null
        ));
    }

    // 유저 이름 수정 테스트
    static UserDto userUpdateTest(UserService userService, UserDto source){
        // 기존 유저의 이름을 새로운 이름으로 변경한다.
        UserUpdateRequest request = new UserUpdateRequest(
                source.id(),
                "후지이 카제",
                null,
                null,
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
                new PublicChannelCreateRequest("음악", null));
    }

    // 메시지 생성 테스트
    static void messageCreateTest(MessageService messageService,
                                  ChannelDto channel, UserDto author){
        MessageDto message = messageService.createMessage(new MessageCreateRequest(
                    "곤니찌와~ ", author.id(), channel.id(), null));

        System.out.println("===== 메시지 생성 =====");
        System.out.println("작성자 : " + author.username());
        System.out.println("메시지 내용 : " + message.content());
        System.out.println("메시지 ID : " + message.id() + "\n");
    }
}