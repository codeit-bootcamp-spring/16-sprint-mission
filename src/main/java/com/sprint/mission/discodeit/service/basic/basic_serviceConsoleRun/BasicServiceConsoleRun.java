package com.sprint.mission.discodeit.service.basic.basic_serviceConsoleRun;

// 엔티티
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

// 레포지터리
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

// 서비스
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

// Basic*Service 구현체를 활용하여 테스트 해보세요
public class BasicServiceConsoleRun {

    public static void basicServiceTest(){

        // 1. Repository 구현체 초기화한다.
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        // 2. Repository를 주입하여 Basic Service 구현체를 초기화한다.
        UserService userService = new BasicUserService(userRepository);

        ChannelService channelService = new BasicChannelService(
                channelRepository, userService);

        MessageService messageService = new BasicMessageService(
                messageRepository, userService, channelService);

        // 3. 유저를 생성하고 정보를 출력한다.
        User user = setupUser(userService);
        System.out.println("\n===== 신규 유저 생성 =====");
        System.out.println("신규 유저 이름 : " + user.getName());
        System.out.println("신규 유저 ID : " + user.getId() + "\n");

        // 3-1. 유저 이름 수정 테스트
        user = userUpdateTest(userService, user);

        // 4. 채널을 생성하고 정보를 출력한다.
        Channel channel = setupChannel(channelService);
        System.out.println("===== 신규 채널 생성 =====");
        System.out.println("신규 채널 이름 : " + channel.getName());
        System.out.println("신규 채널 ID : " + channel.getId());

        // 5. 유저를 채널에 참여시킨다.
        channelService.joinChannel(channel.getId(), user.getId());
        System.out.println(user.getName() + " 유저, " + channel.getName() + " 채널 참여 완료" + "\n");

        // 5. 유저의 채널 참여 여부를 확인한다.
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

        // 6. 메시지 생성 테스트
        messageCreateTest(messageService, channel, user);

        // 7. 변경된 유저 이름, 채널 출력 테스트
        System.out.println("변경된 유저 ID : " + user.getId() + ", 이름: " + user.getName());
        System.out.println("생성된 채널 ID : " + channel.getId() + ", 이름: " + channel.getName() + " 채널");
    }

    // 유저 생성
    static User setupUser(UserService userService){
        User user = new User("요네즈 켄시");
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
        Message message = new Message("곤니찌와~ ", author.getId(), channel.getId(), null);

        message = messageService.createMessage(message);
        System.out.println("===== 메시지 생성 =====");
        System.out.println("작성자 : " + author.getName());
        System.out.println("메시지 내용 : " + message.getContent());
        System.out.println("메시지 ID : " + message.getId() + "\n");
    }
}