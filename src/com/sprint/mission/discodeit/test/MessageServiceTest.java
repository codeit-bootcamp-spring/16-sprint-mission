package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class MessageServiceTest {

    private final MessageService messageService;
    private final UUID authorId;
    private final UUID channelId;

    public MessageServiceTest(
            MessageService messageService,
            UUID authorId,
            UUID channelId
    ) {
        this.messageService = messageService;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public void testCreate() {
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

        System.out.println("메시지 생성 테스트 통과");
    }

    public void testFindAllByChannelId() {
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

        System.out.println("채널별 메시지 조회 테스트 통과");
    }

    public void testUpdate() {
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

        System.out.println("메시지 수정 테스트 통과");
    }

    public void testDelete() {
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

        System.out.println("메시지 삭제 테스트 통과");
    }
}