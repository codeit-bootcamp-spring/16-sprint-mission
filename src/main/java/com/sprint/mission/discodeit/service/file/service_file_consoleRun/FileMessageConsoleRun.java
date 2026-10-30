package com.sprint.mission.discodeit.service.file.service_file_consoleRun;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/* File*Service */
// - "저장 방식": .ser 파일에 직렬화 보관, "비즈니스 방식": 기본적으로 같은 로직, "저장 로직": 파일 읽기-쓰기 코드 추가

public class FileMessageConsoleRun {

    // 전달받은 이름과 일치하는 유저를 조회해 반환한다.
    private static User findUserByName(UserService userService, String name) {
        return userService.readAllUser().stream()
                // 전달받은 이름과 같은 유저만 남긴다.
                .filter(user -> user.getName().equals(name))
                // 조건에 맞는 첫 번쨰 유저를 찾는다.
                .findFirst()
                // 일치하는 유저가 없으면 예외 발생시킨다.
                .orElseThrow(() ->
                        new IllegalArgumentException(name + " 유저를 찾을 수 없습니다."));
    }

    /* 1. 파일 메시지 서비스 구현체를 매개변수로 생성한다. (상위 타입으로)  */
    public static void fileMessageInformation(UserService userService,
                                               ChannelService channelService, MessageService messageService){

        System.out.println("####### 메시지 CRUD 테스트 #######\n");
        // 유저들을 채널에 참여시키기 위해 새로운 객체를 생성한다.
        User planner = findUserByName(userService, "최명함");
        User backendDeveloper = findUserByName(userService, "박코딩");
        User javaDeveloper = findUserByName(userService, "김자바");

        // 유저들이 "개발 커뮤니티" 채널에 각각 서비스 등록을 한다.
        Channel channelV1 = channelService.readAllChannel().stream()
                .filter(channel -> channel.getName().equals("개발 커뮤니티"))
                .findFirst()
                .orElseGet(() ->
                        channelService.createChannel(new Channel("개발 커뮤니티")));

        String beforeJoinContent = "참여 전에 작성한 메시지";

        // 채널에 참여하지 않은 "최명함" 의 메시지 생성을 시도한다.
        try {
            // 메시지 내용, 작성자 ID, 채널 ID를 전달해 메시지를 생성한다.
            Message rejectMessage = new Message(beforeJoinContent, planner.getId(), channelV1.getId(), null);

            messageService.createMessage(rejectMessage);
            System.out.println("검사 실패 : 메시지가 생성되었습니다.");
        } catch (IllegalArgumentException exception){
            // 채널 미참여로 메시지 생성이 거부된 결과를 출력한다.
            System.out.println("\'" + beforeJoinContent +  "\' 생성 거부 : " + exception.getMessage());
        }

        // (최명함, 박코딩, 이자바)가 "개발 커뮤니티" 채널에 참여시킨다.
        System.out.println("입장 대상: \"" + planner.getName() + "\"");
        channelService.joinChannel(channelV1.getId(), planner.getId()); // 최명함 채널 참가.
        System.out.println("\'" + planner.getName() + "\' 유저가 \'"
                + channelV1.getName() + "\' 채널에 입장했습니다.");

        System.out.println("입장 대상: \"" + backendDeveloper.getName() + "\"");
        // 박코딩 개발 커뮤니티 채널 참가
        channelService.joinChannel(channelV1.getId(), backendDeveloper.getId());
        System.out.println("\'" + backendDeveloper.getName() + "\' 유저가 \'"
                + channelV1.getName() + "\' 채널에 입장했습니다.");

        System.out.println("입장 대상: \"" + javaDeveloper.getName() + "\"");
        channelService.joinChannel(channelV1.getId(), javaDeveloper.getId());
        System.out.println("\'" + javaDeveloper.getName() + "\' 유저가 \'"
                + channelV1.getName() + "\' 채널에 입장했습니다.");

        System.out.println();

        /* 2. Message 객체를 생성하고 메시지 내용을 등록한다. */
        Message message1 = new Message("안녕하세요", planner.getId(), channelV1.getId(), null);
        Message message2 = new Message("반갑습니다", planner.getId(), channelV1.getId(), null);
        Message message3 = new Message("오늘 회의 안내입니다", planner.getId(), channelV1.getId(), null);
        messageService.createMessage(message1);
        messageService.createMessage(message2);
        messageService.createMessage(message3);

        System.out.println("==== 개발 커뮤니티 입장 유저 목록 ====");
        String joinUserNames = channelV1.getMemberIds().stream()
                // 유저들을 단건으로 읽는다.
                .map(userService::readUser)
                // 유저들의 이름을 가져온다.
                .map(User::getName)
                .collect(Collectors.joining(", "));
        System.out.println(joinUserNames);
        System.out.println("=".repeat(31));
        System.out.println();

        // "최명하" 를 채널에 참여시킨 뒤 퇴장시킨다. 그리고 해당 채널에서 "최명하"의 참여 정보를 삭제한다.
        System.out.println("퇴장 대상 : \"" + planner.getName() + "\"");
        channelService.leaveChannel(channelV1.getId(), planner.getId());
        System.out.println("\"" + planner.getName() + "\" 유저가 \""
                + channelV1.getName() + "\" 채널에서 퇴장했습니다.");

        System.out.println("==== 개발 커뮤니티 퇴장 후 유저 목록 ====");
        String exitUserNames = channelV1.getMemberIds().stream()
                .map(userService::readUser)
                .map(User::getName)
                .collect(Collectors.joining(", "));

        System.out.println(exitUserNames);
        System.out.println("=".repeat(38));

        String afterLeaveContent = "퇴장 후 작성한 메시지";

        // 퇴장한 "최명하"의 메시지 생성을 시도한다.
        try {
            // 메시지 내용, 작성자 ID, 채널 ID를 전달해 메시지를 생성한다.
            Message rejectedMessage = new Message(afterLeaveContent, planner.getId(), channelV1.getId(), null);

            messageService.createMessage(rejectedMessage);
            System.out.println("검사 실패 : 퇴장한 \"" + planner.getName() + "\"유저의 메시지가 생성되었습니다.");
        } catch (IllegalArgumentException exception){
            // 채널에서 퇴장했기 때문에 메시지 생성이 거부된 결과를 출력한다.
            System.out.println("검사 성공 : " + exception.getMessage());
        }

        System.out.println("\n=== 메시지 생성 결과 ===");
        messageService.readAllMessage().stream()
                .forEach(message ->
                        System.out.println
                                ("\"" +message.getContent()+ "\" 라는 내용을 생성하였습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        /* 3. 각각의 메시지 정보를 단건으로 조회한다. */
        System.out.println("==== 단건 조회 ====");
        System.out.print(messageService.readMessage(message1.getId()));
        System.out.print(messageService.readMessage(message2.getId()));
        System.out.print(messageService.readMessage(message3.getId()));
        System.out.println("=".repeat(17));
        System.out.println();

        /* 4. 메시지 내용을 수정한다. (수정 전과 수정 후) */
        List<String> beforeContents = messageService.readAllMessage()
                .stream()
                .map(Message::getContent)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 메시지 내용 리스트 ===");
        System.out.println(String.join(", ", beforeContents));

        // 여기에서 각 메시지의 새 내용을 직접 지정해 수정한다.
        messageService.updateMessage(message1.getId(), "안녕하세요!");
        messageService.updateMessage(message2.getId(), "반갑습니다!");

        // 수정 후 메시지 내용 목록
        List<String> afterContents = messageService.readAllMessage()
                .stream()
                .map(Message::getContent)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 메시지 내용 리스트 ===");
        System.out.println(String.join(", ", afterContents));

        /* 5. 수정된 메시지 데이터를 조회한다. */
        List<Message> updatedMessages = messageService.readAllMessage().stream()
                .filter(message -> !beforeContents.contains(message.getContent()))
                .collect(Collectors.toList());

        System.out.println("====== 수정된 메시지 명단 ======");
        updatedMessages.stream()
                .forEach(message -> System.out.print(message));

        System.out.println("=".repeat(26));
        System.out.println();

        /* 6. 수정 후 전체 메시지 목록을 조회한다. */
        System.out.println("=== 수정 이후 전체 메시지 조회 ===");
        messageService.readAllMessage().stream()
                .forEach(message -> System.out.print(message));

        System.out.println("=".repeat(27));
        System.out.println();

        /* 7. 메시지를 삭제한다. */
        System.out.println("삭제할 메시지: \"" + message2.getContent() + "\"");
        messageService.deleteMessage(message2.getId());

        /* 8. 삭제한 메시지가 더 이상 조회되지 않는지 확인한다. */
        try {
            messageService.readMessage(message2.getId());
            System.out.println("삭제 확인 실패: \""
                    + message2.getContent() + "\" 라는 메시지가 여전히 존재합니다.");
        } catch (IllegalArgumentException exception) {
            System.out.println("삭제 확인 성공: \""
                    + message2.getContent() + "\" 라는 메시지가 더 이상 조회되지 않습니다.");
        }

        /* 9. 삭제 후 전체 메시지를 조회한다. */
        System.out.println("=== 삭제 후 메시지 목록 ===");
        messageService.readAllMessage().stream()
                .forEach(message -> System.out.print(message));

        System.out.println("=".repeat(22));
    }
}
