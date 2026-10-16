package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.ServiceFactory;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.UUID;

public class MessageServiceTest {

    public static void main(String[] args) {
        UserService userService = ServiceFactory.userService();
        ChannelService channelService = ServiceFactory.channelService();
        MessageService messageService = ServiceFactory.messageService();


        User user = new User("메시지 테스트 사용자", "message@example.com");
        userService.create(user);

        Channel channel = channelService.create(
                "메시지 테스트 채널",
                "메시지 테스트용 채널"
        );
        Message message = messageService.create(
                "첫 번째 메시지",
                user.getId(),
                channel.getId()
        );
        Message foundMessage = messageService.findById(message.getId())
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (messageService.findById(message.getId()).isPresent()) {
            System.out.println("메시지 등록 테스트: 통과");
        } else {
            throw new AssertionError("메시지 등록 테스트 실패");
        }

        boolean messageFound = messageService.findAll().stream()
                .anyMatch(item ->
                        item.getId().equals(message.getId())
                                && "첫 번째 메시지".equals(item.getContent()));

        if (messageFound) {
            System.out.println("메시지 조회 테스트: 통과");
        } else {
            throw new AssertionError("메시지 조회 테스트 실패");
        }

        messageService.update(message.getId(), "수정된 메시지");

        Message updatedMessage = messageService.findById(message.getId())
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if ("수정된 메시지".equals(updatedMessage.getContent()))  {
            System.out.println("메시지 수정 테스트: 통과");
        } else {
            throw new AssertionError("메시지 수정 테스트 실패");
        }

        messageService.delete(message.getId());
        if (messageService.findById(message.getId()).isEmpty()) {
            System.out.println("메시지 삭제 테스트: 통과");
        }else {
            throw new AssertionError("메시지 삭제 테스트 실패");
        }


        // 잘못된 사용자 검증
        try {
            messageService.create(
                    "잘못된 사용자 메시지",
                    UUID.randomUUID(),
                    channel.getId()
            );
            throw new AssertionError(
                    "잘못된 사용자 검증 실패: 존재하지 않는 사용자가 허용됨"
            );
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 사용자 검증: 통과");
        }

        // 잘못된 채널 검증
        try {
            messageService.create(
                    "잘못된 채널 메시지",
                    user.getId(),
                    UUID.randomUUID()
            );
            throw new AssertionError(
                    "잘못된 채널 검증 실패: 존재하지 않는 채널이 허용됨"
            );
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 채널 검증: 통과");
        }
    }
}
