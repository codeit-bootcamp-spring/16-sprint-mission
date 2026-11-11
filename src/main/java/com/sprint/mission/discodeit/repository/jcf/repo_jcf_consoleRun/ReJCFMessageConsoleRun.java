package com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.channelRequest.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFUserStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.*;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReJCFMessageConsoleRun {
    /*
     * JCF Repository CRUD 테스트
     * 저장 방식: HashMap에 보관
     * 저장 로직: 메모리의 Map만 변경
     */
    public static void printMessageRepositoryInformation() {

        System.out.println("####### 메시지 JCF Repository 테스트 #######\n");

        /* 1. Repository 초기화 */
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();
        UserRepository userRepository = new JCFUserRepository();

        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        /* 1-2. Service 초기화 */
        UserService userService =
                new BasicUserService(
                        userRepository, binaryContentRepository, userStatusRepository, passwordEncoder);

        ChannelService channelService =
                new BasicChannelService(
                        channelRepository, userRepository, messageRepository, readStatusRepository);

        MessageService messageService =
                new BasicMessageService(
                        binaryContentRepository, messageRepository, userRepository, channelRepository);

        // 2. 유저 및 채널 생성
        String uniqueId = UUID.randomUUID().toString();

        UserDto planner = userService.createUser(
                new UserCreateRequest(
                        "작성자-" + uniqueId,
                        "planner" + uniqueId + "@test.com",
                        "Password1q2w3e4r", null));

        UserDto developer = userService.createUser(new UserCreateRequest(
                "안진호-" + uniqueId,
                "develop-" + uniqueId + "@test.com",
                "Engin1q2w3e4r",
                null));

        ChannelDto channel = channelService.createPublicChannel(
                new PublicChannelCreateRequest("JCF 메시지 테스트 채널" + uniqueId, null));

        UUID plannerId = planner.id();
        UUID developerId = developer.id();
        UUID channelId = channel.id();

        // 생성한 유저를 채널에 가입시킨다.
        channelService.joinChannel(channelId, plannerId);
        channelService.joinChannel(channel.id(), developer.id());

        // 3. Message 객체 생성 및 저장
        MessageDto message1 = messageService.createMessage(
                new MessageCreateRequest(
                        "안녕하세요!", plannerId, channelId,null));
        MessageDto message2 = messageService.createMessage(
                new MessageCreateRequest(
                        "자바 공부 중입니다.", developerId , channelId,null));
        MessageDto message3 = messageService.createMessage(
                new MessageCreateRequest(
                        "Repository 테스트입니다.", plannerId, channelId,null));
        MessageDto message4 = messageService.createMessage(
                new MessageCreateRequest(
                        "삭제할 메시지입니다.", plannerId, channelId,null));

        // 4. 전체 메시지 조회
        System.out.println("=== 메시지 생성 결과 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                        .forEach(message ->
                                System.out.println(message.content() + " 메시지가 생성되었습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        // 5. 특정 메시지 조회
        System.out.println("=== 단건 조회 ===");
        System.out.print(messageService.readMessage(message1.id()));
        System.out.print(messageService.readMessage(message2.id()));
        System.out.print(messageService.readMessage(message3.id()));
        System.out.print(messageService.readMessage(message4.id()));

        System.out.println("=".repeat(17));
        System.out.println();

        // 6. 메시지 수정 전 목록
        List<String> beforeContents = messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .map(MessageDto::content)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 메시지 목록 ===");
        System.out.println(String.join(", ", beforeContents));

        // 7. 메시지 수정
        messageService.updateMessage(
                new MessageUpdateRequest(message1.id(), planner.id(), "수정된 안녕하세요!"));
        messageService.updateMessage(
                new MessageUpdateRequest(message2.id(), developer.id(), "수정된 자바 공부 중입니다."));

        // 8. 메시지 수정 후 목록
        List<String> afterContents = messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .map(MessageDto::content)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 메시지 목록 ===");
        System.out.println(String.join(", ", afterContents));

        // 9. 수정된 메시지 확인
        List<MessageDto> updatedMessageInfo = messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .filter(message -> !beforeContents.contains(message.content()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 메시지 명단 ===");
        updatedMessageInfo.forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 10. 전체 메시지 조회
        System.out.println("=== 전체 메시지 조회 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 11. 메시지 삭제
        System.out.println("삭제 대상: \"" + message4.content() + "\"");
        messageService.deleteMessage(message4.id(), plannerId);

        // 12. 삭제 결과 확인
        System.out.println("=== 삭제 후 메시지 목록 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .forEach(System.out::println);

        System.out.println("=".repeat(22));
        System.out.println();
    }
}