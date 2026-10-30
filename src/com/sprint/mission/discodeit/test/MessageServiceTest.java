package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.DiscodeitApplication;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

public class MessageServiceTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        BinaryContentRepository binaryContentRepository =
                context.getBean(BinaryContentRepository.class);

        String testId = UUID.randomUUID().toString().substring(0, 8);

        UUID userId = null;
        UUID channelId = null;

        try {
            // 테스트용 사용자 생성
            UserDto user = userService.create(
                    new UserCreateRequest(
                            "메시지 테스트 사용자 " + testId,
                            "message-test-" + testId + "@example.com",
                            "password123",
                            null,
                            null,
                            null
                    )
            );

            userId = user.id();

            // 테스트용 공개 채널 생성
            ChannelDto channel = channelService.createPublic(
                    new ChannelCreateRequest(
                            "메시지 테스트 채널 " + testId,
                            "메시지 테스트용 채널"
                    )
            );

            channelId = channel.id();

            // 기존 메시지 CRUD 테스트
            testCreate(messageService, userId, channelId);
            testFindAllByChannelId(messageService, userId, channelId);
            testUpdate(messageService, userId, channelId);
            testDelete(messageService, userId, channelId);

            // 여러 첨부파일 동시 업로드 및 삭제 테스트
            testMultipleAttachments(
                    messageService,
                    binaryContentRepository,
                    userId,
                    channelId
            );

        } finally {
            // 테스트 데이터 정리
            if (channelId != null) {
                channelService.delete(channelId);
            }

            if (userId != null) {
                userService.deleteById(userId);
            }

            context.close();
        }
    }

    // 메시지 생성 테스트
    private static void testCreate(
            MessageService messageService,
            UUID authorId,
            UUID channelId
    ) {
        Message message = messageService.create(
                new MessageCreateRequest(
                        "테스트 메시지",
                        authorId,
                        channelId,
                        List.of()
                )
        );

        if (message == null) {
            throw new AssertionError("메시지 생성에 실패했습니다.");
        }

        if (!"테스트 메시지".equals(message.getContent())) {
            throw new AssertionError("메시지 내용이 일치하지 않습니다.");
        }

        System.out.println("메시지 생성 테스트: 통과");
    }

    // 채널별 메시지 조회 테스트
    private static void testFindAllByChannelId(
            MessageService messageService,
            UUID authorId,
            UUID channelId
    ) {
        Message message = messageService.create(
                new MessageCreateRequest(
                        "채널 조회 테스트",
                        authorId,
                        channelId,
                        List.of()
                )
        );

        List<Message> messages =
                messageService.findAllByChannelId(channelId);

        boolean messageFound = messages.stream()
                .anyMatch(item -> item.getId().equals(message.getId()));

        if (!messageFound) {
            throw new AssertionError("해당 채널의 메시지를 찾지 못했습니다.");
        }

        System.out.println("채널별 메시지 조회 테스트: 통과");
    }

    // 메시지 수정 테스트
    private static void testUpdate(
            MessageService messageService,
            UUID authorId,
            UUID channelId
    ) {
        Message message = messageService.create(
                new MessageCreateRequest(
                        "수정 전 메시지",
                        authorId,
                        channelId,
                        List.of()
                )
        );

        Message updatedMessage = messageService.update(
                new MessageUpdateRequest(
                        message.getId(),
                        "수정된 메시지"
                )
        );

        if (!"수정된 메시지".equals(updatedMessage.getContent())) {
            throw new AssertionError("메시지 수정에 실패했습니다.");
        }

        System.out.println("메시지 수정 테스트: 통과");
    }

    // 메시지 삭제 테스트
    private static void testDelete(
            MessageService messageService,
            UUID authorId,
            UUID channelId
    ) {
        Message message = messageService.create(
                new MessageCreateRequest(
                        "삭제 테스트 메시지",
                        authorId,
                        channelId,
                        List.of()
                )
        );

        messageService.delete(message.getId());

        if (messageService.findById(message.getId()).isPresent()) {
            throw new AssertionError("메시지 삭제에 실패했습니다.");
        }

        System.out.println("메시지 삭제 테스트: 통과");
    }

    /**
     * 여러 첨부파일 동시 업로드 및 메시지 삭제 테스트
     */
    private static void testMultipleAttachments(
            MessageService messageService,
            BinaryContentRepository binaryContentRepository,
            UUID authorId,
            UUID channelId
    ) {
        // 테스트용 첨부파일 데이터
        byte[] firstFileData = "first-attachment".getBytes();
        byte[] secondFileData = "second-attachment".getBytes();

        BinaryContentCreateRequest firstAttachment =
                new BinaryContentCreateRequest(
                        "first.txt",
                        "text/plain",
                        firstFileData.length,
                        firstFileData
                );

        BinaryContentCreateRequest secondAttachment =
                new BinaryContentCreateRequest(
                        "second.txt",
                        "text/plain",
                        secondFileData.length,
                        secondFileData
                );

        // 메시지 생성과 동시에 첨부파일 2개 업로드
        Message message = messageService.create(
                new MessageCreateRequest(
                        "첨부파일 동시 업로드 테스트",
                        authorId,
                        channelId,
                        List.of(firstAttachment, secondAttachment)
                )
        );

        if (message == null) {
            throw new AssertionError("첨부파일 메시지 생성에 실패했습니다.");
        }

        // 메시지에 첨부파일 2개가 연결됐는지 확인
        List<UUID> attachmentIds = message.getAttachmentIds();

        if (attachmentIds.size() != 2) {
            throw new AssertionError(
                    "첨부파일 개수가 2개가 아닙니다. 실제 개수: "
                            + attachmentIds.size()
            );
        }

        System.out.println("첨부파일 2개 동시 업로드: 통과");

        // 첨부파일이 실제 저장소에 존재하는지 확인
        for (UUID attachmentId : attachmentIds) {
            if (binaryContentRepository.findById(attachmentId).isEmpty()) {
                throw new AssertionError(
                        "첨부파일이 저장소에 존재하지 않습니다: " + attachmentId
                );
            }
        }

        System.out.println("첨부파일 저장 여부 확인: 통과");

        // 메시지 삭제
        UUID messageId = message.getId();

        messageService.delete(messageId);

        // 메시지가 삭제됐는지 확인
        if (messageService.findById(messageId).isPresent()) {
            throw new AssertionError("메시지가 삭제되지 않았습니다.");
        }

        System.out.println("첨부파일 메시지 삭제: 통과");

        // 첨부파일 2개도 모두 삭제됐는지 확인
        for (UUID attachmentId : attachmentIds) {
            if (binaryContentRepository.findById(attachmentId).isPresent()) {
                throw new AssertionError(
                        "메시지 삭제 후 첨부파일이 남아 있습니다: " + attachmentId
                );
            }
        }

        System.out.println("메시지 삭제 시 첨부파일 2개 삭제: 통과");
    }
}