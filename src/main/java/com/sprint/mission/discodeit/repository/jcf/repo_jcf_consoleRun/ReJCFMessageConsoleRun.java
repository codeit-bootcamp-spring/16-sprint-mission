package com.sprint.mission.discodeit.repository.jcf.repo_jcf_consoleRun;

import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

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

        // 1. Repository와 Service 초기화
        MessageRepository messageRepository = new JCFMessageRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        UserRepository userRepository = new JCFUserRepository();

        UserService userService =
                new BasicUserService(userRepository);
        ChannelService channelService =
                new BasicChannelService(channelRepository, userService);
        MessageService messageService =
                new BasicMessageService(messageRepository, userService, channelService);

        // 2. Message 객체 생성 및 저장
        UUID plannerId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        Message message1 = new Message("안녕하세요!", plannerId, channelId, null);
        Message message2 = new Message("자바 공부 중입니다.", plannerId, channelId, null);
        Message message3 = new Message("Repository 테스트입니다.", plannerId, channelId, null);
        Message message4 = new Message("삭제할 메시지입니다.", plannerId, channelId, null);

        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);
        messageRepository.save(message4);

        // 2. 전체 메시지 조회
        System.out.println("=== 메시지 생성 결과 ===");
        messageService.readAllMessage().forEach(message ->
                System.out.println(message.getContent() + " 메시지가 생성되었습니다."));
        System.out.println("=".repeat(19));
        System.out.println();

        // 3. 특정 메시지 조회
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