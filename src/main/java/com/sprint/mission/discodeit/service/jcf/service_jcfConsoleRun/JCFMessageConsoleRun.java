package com.sprint.mission.discodeit.service.jcf.service_jcfConsoleRun;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.channelRequest.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userRequest.UserCreateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.otherRepository.UserStatusRepository;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.otherJCF.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/* JCF*Service */
// - "저장 방식": ArrayList 에 보관, "비즈니스 방식": (중복 확인, ID로 조회, 입력 수정, 삭제), "저장 로직": 메모리 목록만 변경

public class JCFMessageConsoleRun {
    /*
     * 메시지 CRUD 테스트를 위해 구현한 메서드입니다.
     */
    public static void printMessageInformation() {

        System.out.println("####### 메시지 CRUD 테스트 #######\n");

        /* 1. Repository 구현체를 생성한다. */
        MessageRepository messageRepository = new JCFMessageRepository();
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();

        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();

        /* 2. Repository를 주입하여 Service 구현체를 생성한다. */
        UserService userService = new JCFUserService(
                userRepository, binaryContentRepository, userStatusRepository);

        ChannelService channelService = new JCFChannelService(
                userRepository, readStatusRepository, messageRepository);

        MessageService messageService = new JCFMessageService(userRepository, channelRepository);

        String uniqueId = UUID.randomUUID().toString();

        // 유저들을 채널에 참여시키기 위해 새로운 객체를 생성한다.
        UserDto planner = userService.createUser(new UserCreateRequest(
                "최명함-" + uniqueId,
                "planner-" + uniqueId + "@test.com",
                "Cmh1q2w3e4r", null));
        UserDto backendDeveloper = userService.createUser(new UserCreateRequest(
                "박코딩-" + uniqueId,
                "backend-" + uniqueId + "@test.com",
                "Pkd1q2w3e4r", null));
        UserDto javaDeveloper = userService.createUser(new UserCreateRequest(
                "이자바-" + uniqueId,
                "java-" + uniqueId + "@test.com",
                "Kjb1q2w3e4r", null));

        // 유저들이 참여할 PUBLIC 채널 생성
        ChannelDto channelV1 = channelService.createPublicChannel(
                new PublicChannelCreateRequest("개발 커뮤니티-" + uniqueId, null));

        String beforeJoinContent = "참여 전에 작성한 메시지";

        // 채널에 참여하지 않은 "최명함" 의 메시지 생성을 시도한다.
        try {
            messageService.createMessage(new MessageCreateRequest(
                    beforeJoinContent, planner.id(), channelV1.id(), null));

            System.out.println("검사 실패 : 메시지가 생성되었습니다.");
        } catch (IllegalArgumentException iaException){
            // 채널 미참여로 메시지 생성이 거부된 결과를 출력한다.
            System.out.println("\'" + beforeJoinContent +  "\' 생성 거부 : " + iaException.getMessage());
        }

        // (최명함, 박코딩, 이자바)가 "개발 커뮤니티" 채널에 참여시킨다.
        System.out.println("입장 대상: \"" + planner.username() + "\"");
        channelService.joinChannel(channelV1.id(), planner.id());
        System.out.println("\'" + planner.username() + "\' 유저가 \'"
                + channelV1.channelName() + "\' 채널에 입장했습니다.");

        System.out.println("입장 대상: \"" + backendDeveloper.username() + "\"");
        channelService.joinChannel(channelV1.id(), backendDeveloper.id());
        System.out.println("\'" + backendDeveloper.username() + "\' 유저가 \'"
                + channelV1.channelName() + "\' 채널에 입장했습니다.");

        System.out.println("입장 대상: \"" + javaDeveloper.username() + "\"");
        channelService.joinChannel(channelV1.id(), javaDeveloper.id());
        System.out.println("\'" + javaDeveloper.username() + "\' 유저가 \'"
                + channelV1.channelName() + "\' 채널에 입장했습니다.");

        System.out.println();

        /* 2. Message 객체를 생성하고 메시지를 등록한다. */
        MessageDto message1 = messageService.createMessage(new MessageCreateRequest(
                        "안녕하세요", javaDeveloper.id(), channelV1.id(), null));

        MessageDto message2 = messageService.createMessage(new MessageCreateRequest(
                        "반갑습니다", backendDeveloper.id(), channelV1.id(), null));

        MessageDto message3 = messageService.createMessage(new MessageCreateRequest(
                        "오늘 회의 안내입니다", planner.id(), channelV1.id(), null));

        System.out.println("==== 개발 커뮤니티 입장 유저 목록 ====");
        String joinUserNames = channelService.readChannel(channelV1.id())
                .memberIds().stream()
                .map(userService::readUser)
                .map(UserDto::username)
                .collect(Collectors.joining(", "));

        System.out.println(joinUserNames);
        System.out.println("=".repeat(31));
        System.out.println();

        // "최명하" 를 채널에 참여시킨 뒤 퇴장시킨다. 그리고 해당 채널에서 "최명하"의 참여 정보를 삭제한다.
        System.out.println("퇴장 대상 : \"" + planner.username() + "\"");
        channelService.leaveChannel(channelV1.id(), planner.id());
        System.out.println("\"" + planner.username() + "\" 유저가 \""
                + channelV1.channelName() + "\" 채널에서 퇴장했습니다.");

        System.out.println("==== 개발 커뮤니티 퇴장 후 유저 목록 ====");
        String exitUserNames = channelService.readChannel(channelV1.id())
                .memberIds().stream()
                .map(userService::readUser)
                .map(UserDto::username)
                .collect(Collectors.joining(", "));

        System.out.println(exitUserNames);
        System.out.println("=".repeat(38));

        String afterLeaveContent = "퇴장 후 작성한 메시지";

        // 퇴장한 "최명하"의 메시지 생성을 시도한다.
        try {
            // 메시지 내용, 작성자 ID, 채널 ID를 전달해 메시지를 생성한다.
            messageService.createMessage(new MessageCreateRequest(
                    afterLeaveContent, planner.id(), channelV1.id(), null));

            System.out.println("검사 실패 : 퇴장한 \"" + planner.username() + "\" 유저의 메시지가 생성되었습니다.");
        } catch (IllegalArgumentException exception){
            // 채널에서 퇴장했기 때문에 메시지 생성이 거부된 결과를 출력한다.
            System.out.println("검사 성공 : " + exception.getMessage());
        }

        System.out.println("\n=== 메시지 생성 결과 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelV1.id()))
                .forEach(message ->
                        System.out.println
                                ("\"" + message.content() + "\" 라는 내용을 생성하였습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        /* 3. 각각의 메시지 정보를 단건으로 조회한다. */
        System.out.println("==== 단건 조회 ====");
        System.out.print(messageService.readMessage(message1.id()));
        System.out.print(messageService.readMessage(message2.id()));
        System.out.print(messageService.readMessage(message3.id()));
        System.out.println("=".repeat(17));
        System.out.println();

        /* 4. 메시지 내용을 수정한다. (수정 전과 수정 후) */
        List<String> beforeContents = messageService.readAllMessage()
                .stream()
                .filter(message -> message.channelId().equals(channelV1.id()))
                .map(MessageDto::content)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 메시지 내용 리스트 ===");
        System.out.println(String.join(", ", beforeContents));

        // 여기에서 각 메시지의 새 내용을 직접 지정해 수정한다.
        messageService.updateMessage(new MessageUpdateRequest(message1.id(), javaDeveloper.id(),"안녕하세요!"));
        messageService.updateMessage(new MessageUpdateRequest(message2.id(), backendDeveloper.id(),"반갑습니다!"));

        // 수정 후 메시지 내용 목록
        List<String> afterContents = messageService.readAllMessage()
                .stream()
                .filter(message -> message.channelId().equals(channelV1.id()))
                .map(MessageDto::content)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 메시지 내용 리스트 ===");
        System.out.println(String.join(", ", afterContents));

        /* 5. 수정된 메시지 데이터를 조회한다. */
        List<MessageDto> updatedMessages = messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelV1.id()))
                .filter(message -> !beforeContents.contains(message.content()))
                .collect(Collectors.toList());

        System.out.println("====== 수정된 메시지 명단 ======");
        updatedMessages.stream()
                .forEach(message -> System.out.print(message));

        System.out.println("=".repeat(26));
        System.out.println();

        /* 6. 수정 후 전체 메시지 목록을 조회한다. */
        System.out.println("=== 수정 이후 전체 메시지 조회 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelV1.id()))
                .forEach(message -> System.out.print(message));

        System.out.println("=".repeat(27));
        System.out.println();

        /* 7. 메시지를 삭제한다. */
        System.out.println("삭제할 메시지: \"" + message2.content() + "\"");
        messageService.deleteMessage(message2.id(), backendDeveloper.id());

        /* 8. 삭제한 메시지가 더 이상 조회되지 않는지 확인한다. */
        try {
            messageService.readMessage(message2.id());
            System.out.println("삭제 확인 실패: \""
                    + message2.content() + "\" 라는 메시지가 여전히 존재합니다.");
        } catch (IllegalArgumentException exception) {
            System.out.println("삭제 확인 성공: \""
                    + message2.content() + "\" 라는 메시지가 더 이상 조회되지 않습니다.");
        }

        /* 9. 삭제 후 전체 메시지를 조회한다. */
        System.out.println("=== 삭제 후 메시지 목록 ===");
        messageService.readAllMessage().stream()
                .filter(message -> message.channelId().equals(channelV1.id()))
                .forEach(message -> System.out.print(message));

        System.out.println("=".repeat(22));
    }
}