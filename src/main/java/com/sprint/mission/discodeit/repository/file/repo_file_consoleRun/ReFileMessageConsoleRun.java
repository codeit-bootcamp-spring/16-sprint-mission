package com.sprint.mission.discodeit.repository.file.repo_file_consoleRun;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.channelRequest.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.repository.file.otherFile.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.otherFile.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.otherFile.FileUserStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.*;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReFileMessageConsoleRun {
    /*
     * File Repository CRUD 테스트
     * 저장 방식: 파일에 직렬화하여 저장
     * 저장 로직: 파일에서 데이터를 불러오고, 변경된 목록을 파일에 저장
     */
    public static void printFileMessageRepositoryInformation() {

        System.out.println("####### 메시지 File Repository 테스트 #######\n");

        /* 1. Repository 초기화 */
        UserRepository userRepository = new FileUserRepository(".discodeit");
        ChannelRepository channelRepository = new FileChannelRepository(".discodeit");
        MessageRepository messageRepository = new FileMessageRepository(".discodeit");
        ReadStatusRepository readStatusRepository = new FileReadStatusRepository(".discodeit");

        UserStatusRepository userStatusRepository = new FileUserStatusRepository(".discodeit");
        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository(".discodeit");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        /* 1-2. Service 초기화 */
        UserService userService = new BasicUserService(
                userRepository, binaryContentRepository, userStatusRepository, passwordEncoder);

        ChannelService channelService = new BasicChannelService(
                channelRepository, userRepository, messageRepository, readStatusRepository);

        MessageService messageService = new BasicMessageService(
                binaryContentRepository, messageRepository, userRepository, channelRepository);

        /* 2. 테스트용 유저 생성 */
        String uniqueId = UUID.randomUUID().toString();

        UserDto planner = userService.createUser(new UserCreateRequest(
                "작성자-" + uniqueId,
                "planner-" + uniqueId + "@test.com",
                "Plan1q2w3e4r",
                null));

        UserDto developer = userService.createUser(new UserCreateRequest(
                "안진호-" + uniqueId,
                "develop-" + uniqueId + "@test.com",
                "Engin1q2w3e4r",
                null));

        /* 3. PUBLIC 채널 생성 */
        ChannelDto channel = channelService.createPublicChannel(
                new PublicChannelCreateRequest("공지사항-" + uniqueId, null));

        /* 유저를 채널에 참여시킨다. */
        channelService.joinChannel(channel.id(), planner.id());
        channelService.joinChannel(channel.id(), developer.id());

        /* 실제 존재하는 객체의 ID 사용 */
        UUID plannerId = planner.id();
        UUID developerId = developer.id();
        UUID channelId = channel.id();

        MessageDto message1 = messageService.createMessage(
                new MessageCreateRequest(
                        "안녕하세요!", plannerId, channelId,null));
        MessageDto message2 = messageService.createMessage(
                new MessageCreateRequest(
                        "자바 공부 중입니다.", developerId,channelId,null));
        MessageDto message3 = messageService.createMessage(
                new MessageCreateRequest(
                        "Repository 테스트입니다.", plannerId,channelId,null));
        MessageDto message4 = messageService.createMessage(
                new MessageCreateRequest(
                        "삭제할 메시지입니다.", plannerId, channelId,null));

        /* 3. 메시지 생성 결과 확인 */
        System.out.println("=== 메시지 생성 결과 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .forEach(message ->
                        System.out.println(message.content() + " 메시지가 생성되었습니다."));

        System.out.println("=".repeat(19));
        System.out.println();

        /* 4. 단건 조회 */
        System.out.println("=== 단건 조회 ===");
        System.out.println(messageService.readMessage(message1.id()));
        System.out.println(messageService.readMessage(message2.id()));
        System.out.println(messageService.readMessage(message3.id()));
        System.out.println(messageService.readMessage(message4.id()));

        System.out.println("=".repeat(17));
        System.out.println();

        /* 5. 메시지 수정 전 목록 */
        List<String> beforeContents = messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .map(MessageDto::content)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 메시지 목록 ===");
        System.out.println(String.join(", ", beforeContents) + "\n");

        /* 6. 메시지 수정 */
        messageService.updateMessage(
                new MessageUpdateRequest(message1.id(), planner.id(), "수정된 안녕하세요!"));
        messageService.updateMessage(
                new MessageUpdateRequest(message2.id(), developer.id(),"수정된 자바 공부 중입니다."));

        /* 7. 메시지 수정 후 목록 */
        List<String> afterContents = messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .map(MessageDto::content)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 메시지 목록 ===");
        System.out.println(String.join(", ", afterContents) + "\n");

        /* 8. 수정된 메시지 확인 */
        List<MessageDto> updatedMessageInfo = messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .filter(message -> !beforeContents.contains(message.content()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 메시지 명단 ===");
        updatedMessageInfo.forEach(ReFileMessageConsoleRun::printMessageInfo);

        System.out.println("=".repeat(22));
        System.out.println();

        /* 9. 전체 메시지 조회 */
        System.out.println("=== 전체 메시지 조회 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .forEach(ReFileMessageConsoleRun::printMessageInfo);

        System.out.println("=".repeat(22));
        System.out.println();

        /* 10. 메시지 삭제  */
        System.out.println("삭제 대상: \"" + message4.content() + "\"");
        messageService.deleteMessage(message4.id(), plannerId);

        /* 11. 삭제 결과 확인 */
        System.out.println("=== 삭제 후 메시지 목록 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelId))
                .forEach(ReFileMessageConsoleRun::printMessageInfo);

        System.out.println("=".repeat(22));
        System.out.println();
    }

    // MessageDto 정보 출력
    private static void printMessageInfo(MessageDto message) {
        System.out.println("MessageDto {");
        System.out.println("    id        = " + message.id());
        System.out.println("    content   = " + message.content());
        System.out.println("    userId    = " + message.userId());
        System.out.println("    channelId = " + message.channelId());
        System.out.println("    createdAt = " + message.createdAt());
        System.out.println("    updatedAt = " + message.updatedAt());
        System.out.println("}\n");
    }
}