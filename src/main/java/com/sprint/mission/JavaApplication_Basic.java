package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/** Basic 서비스에 JCF 또는 File Repository를 연결해 같은 업무 흐름을 실행합니다. */
public class JavaApplication_Basic {

    static User setupUser(UserService userService) {
        // 매 실행마다 이메일을 다르게 만들어 File 저장소를 재실행해도 중복되지 않게 합니다.
        String runId = UUID.randomUUID().toString().substring(0, 8);
        return userService.create("woody-" + runId,
                "woody-" + runId + "@codeit.com", "woody1234");
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
    }

    static void messageCreateTest(
            MessageService messageService,
            Channel channel,
            User author
    ) {
        // 템플릿의 인자 순서는 content, channelId, authorId입니다.
        Message message = messageService.create(
                "안녕하세요.", channel.getId(), author.getId()
        );
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        System.out.println("========== Basic 서비스 + JCF Repository ==========");
        // 구현 클래스는 변수 타입이 아니라 인터페이스 타입으로 보관합니다.
        // 이 구성이 JCF 저장소를 선택하는 지점입니다.
        UserRepository jcfUserRepository = new JCFUserRepository();
        ChannelRepository jcfChannelRepository = new JCFChannelRepository();
        MessageRepository jcfMessageRepository = new JCFMessageRepository();

        runScenario(
                new BasicUserService(jcfUserRepository),
                new BasicChannelService(jcfChannelRepository),
                new BasicMessageService(
                        jcfMessageRepository, jcfUserRepository, jcfChannelRepository
                )
        );

        System.out.println("\n========== Basic 서비스 + File Repository ==========");
        // 같은 Basic 서비스에 File 구현체를 넣으면 저장 방식만 파일로 바뀝니다.
        Path directory = Paths.get(
                System.getProperty("user.dir"), "data", "basic-demo"
        );
        UserRepository fileUserRepository = new FileUserRepository(
                directory.resolve("users")
        );
        ChannelRepository fileChannelRepository = new FileChannelRepository(
                directory.resolve("channels")
        );
        MessageRepository fileMessageRepository = new FileMessageRepository(
                directory.resolve("messages")
        );

        runScenario(
                new BasicUserService(fileUserRepository),
                new BasicChannelService(fileChannelRepository),
                new BasicMessageService(
                        fileMessageRepository, fileUserRepository, fileChannelRepository
                )
        );
    }

    private static void runScenario(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        System.out.println("실행 전 사용자 수: " + userService.findAll().size());
        System.out.println("실행 전 채널 수: " + channelService.findAll().size());
        System.out.println("실행 전 메시지 수: " + messageService.findAll().size());

        // 템플릿과 같은 준비 및 검증 흐름을 두 저장 방식에 각각 실행합니다.
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);

        System.out.println("실행 후 사용자 수: " + userService.findAll().size());
        System.out.println("실행 후 채널 수: " + channelService.findAll().size());
        System.out.println("실행 후 메시지 수: " + messageService.findAll().size());
    }
}
