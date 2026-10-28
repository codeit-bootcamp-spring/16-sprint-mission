package com.sprint.mission.discodeit.repository_jcf_consoleRun;

import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;

import java.util.UUID;

public class ReJCFMessageConsoleRun {

    /*
     * JCF Repository CRUD 테스트
     * 저장 방식: ArrayList에 보관
     * 저장 로직: 메모리 목록만 변경
     */
    public static void printMessageRepositoryInformation() {
        System.out.println("####### 메시지 JCF Repository 테스트 #######\n");

        MessageRepository messageRepository = new JCFMessageRepository();

        // 1. 메시지 생성
        UUID plannerId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        Message message1 = new Message("안녕하세요!", plannerId, channelId);
        Message message2 = new Message("자바 공부 중입니다.", plannerId, channelId);
        Message message3 = new Message("Repository 테스트입니다.", plannerId, channelId);
        Message message4 = new Message("삭제할 메시지입니다.", plannerId, channelId);

        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);
        messageRepository.save(message4);

        // 2. 전체 메시지 조회
        System.out.println("1. 전체 메시지 조회");
        messageRepository.findAll().stream()
                .forEach(System.out::print);

        // 3. 특정 메시지 조회
        System.out.println("\n2. 특정 메시지 조회");
        messageRepository.findByMessageId(message1.getId())
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("메시지를 찾을 수 없습니다.")
                );

        // 4. 메시지 존재 여부 확인
        System.out.println("\n3. 메시지 존재 여부 확인");
        System.out.println(
                messageRepository.existsByMessageId(message1.getId())
        );

        // 5. 메시지 수정
        System.out.println("\n4. 메시지 수정");
        message1.changeContent("수정된 안녕하세요!");
        messageRepository.save(message1);

        message2.changeContent("수정된 자바 공부 중입니다.");
        messageRepository.save(message2);

        messageRepository.findAll().stream()
                .forEach(System.out::print);

        // 6. 메시지 삭제
        System.out.println("\n5. 메시지 삭제");
        messageRepository.deleteByMessageId(message4.getId());

        messageRepository.findAll().stream()
                .forEach(System.out::print);

        // 7. 삭제한 메시지의 존재 여부 확인
        System.out.println("\n6. 삭제한 메시지 존재 여부");
        System.out.println(
                messageRepository.existsByMessageId(message4.getId()));
    }
}
