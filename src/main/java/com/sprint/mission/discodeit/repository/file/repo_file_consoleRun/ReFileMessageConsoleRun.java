package com.sprint.mission.discodeit.repository.file.repo_file_consoleRun;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

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

        // 1. Repository와 Service 초기화
        MessageRepository messageRepository = new FileMessageRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        UserRepository userRepository = new FileUserRepository();

        UserService userService =
                new BasicUserService(userRepository);
        ChannelService channelService =
                new BasicChannelService(channelRepository, userService);
        MessageService messageService =
                new BasicMessageService(messageRepository, userService, channelService);

        // 이후 CRUD 테스트는 JCFUserRepositoryRun과 동일한 방식
        // 2. Message 객체 생성 및 저장
        User planner = new User("작성자");
        userService.createUser(planner);

        Channel channel = new Channel("공지사항");
        channelService.createChannel(channel);

        // 유저를 채널에 참여시킨다.
        channelService.joinChannel(channel.getId(), planner.getId());

        // 실제 존재하는 객체의 ID 사용
        UUID plannerId = planner.getId();
        UUID channelId = channel.getId();

        Message message1 = new Message("안녕하세요!", plannerId, channelId, null);
        Message message2 = new Message("자바 공부 중입니다.", plannerId, channelId, null);
        Message message3 = new Message("Repository 테스트입니다.", plannerId, channelId, null);
        Message message4 = new Message("삭제할 메시지입니다.", plannerId, channelId, null);

        messageService.createMessage(message1);
        messageService.createMessage(message2);
        messageService.createMessage(message3);
        messageService.createMessage(message4);

        // 3. 메시지 생성 결과 확인
        System.out.println("=== 메시지 생성 결과 ===");
        messageService.readAllMessage().forEach(message ->
                System.out.println(message.getContent() + " 메시지가 생성되었습니다."));

        System.out.println("=".repeat(19));
        System.out.println();

        // 4. 단건 조회
        System.out.println("=== 단건 조회 ===");
        System.out.print(messageService.readMessage(message1.getId()));
        System.out.print(messageService.readMessage(message2.getId()));
        System.out.print(messageService.readMessage(message3.getId()));
        System.out.print(messageService.readMessage(message4.getId()));

        System.out.println("=".repeat(17));
        System.out.println();

        // 5. 메시지 수정 전 목록
        List<String> beforeContents = messageService.readAllMessage().stream()
                .map(Message::getContent)
                .collect(Collectors.toList());

        System.out.println("=== 수정 전 메시지 목록 ===");
        System.out.println(String.join(", ", beforeContents));

        // 6. 메시지 수정
        messageService.updateMessage(message1.getId(), "수정된 안녕하세요!");
        messageService.updateMessage(message2.getId(), "수정된 자바 공부 중입니다.");

        // 7. 메시지 수정 후 목록
        List<String> afterContents = messageService.readAllMessage().stream()
                .map(Message::getContent)
                .collect(Collectors.toList());

        System.out.println("=== 수정 후 메시지 목록 ===");
        System.out.println(String.join(", ", afterContents));

        // 8. 수정된 메시지 확인
        List<Message> updatedMessageInfo = messageService.readAllMessage().stream()
                .filter(message -> !beforeContents.contains(message.getContent()))
                .collect(Collectors.toList());

        System.out.println("=== 수정된 메시지 명단 ===");
        updatedMessageInfo.forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 9. 전체 메시지 조회
        System.out.println("=== 전체 메시지 조회 ===");
        messageService.readAllMessage().forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();

        // 10. 메시지 삭제
        System.out.println("삭제 대상: \"" + message4.getContent() + "\"");
        messageService.deleteMessage(message4.getId());

        // 11. 삭제 결과 확인
        System.out.println("=== 삭제 후 메시지 목록 ===");
        messageService.readAllMessage().forEach(System.out::print);

        System.out.println("=".repeat(22));
        System.out.println();
    }
}
