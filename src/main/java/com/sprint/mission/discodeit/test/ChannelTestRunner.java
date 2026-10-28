package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class ChannelTestRunner {
    public static void run(
            ChannelService channelService,
            MessageService messageService,
            UserService userService,
            User woody,
            User buzz,
            Channel general
    ) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Channel channel = channelService.createChannel(
                new Channel("채널 테스트 " + suffix, woody.getId()));
        UUID channelId = channel.getId();

        System.out.println("\n=== 채널 테스트 ===");
        try {
            channelService.joinChannel(channelId, buzz.getId());
            checkException("중복 참여", "이미", () -> channelService.joinChannel(channelId, buzz.getId()));
            channelService.leaveChannel(channelId, buzz.getId());
            Channel afterLeave = channelService.getChannelById(channelId);
            User buzzAfterLeave = userService.getUserById(buzz.getId());
            ensure(!afterLeave.getParticipantIds().contains(buzz.getId()), "채널 참여자에서 버즈가 제거되지 않았습니다.");
            ensure(!buzzAfterLeave.getMyChannelIds().contains(channelId), "버즈의 참여 채널 목록에서 채널이 제거되지 않았습니다.");
            System.out.println("버즈 퇴장 후 채널·사용자 관계:");
            printChannel(afterLeave, messageService, userService);

            channelService.joinChannel(channelId, buzz.getId());
            String renamed = "수정된 채널 " + suffix;
            channelService.updateChannelName(channelId, woody.getId(), renamed);
            ensure(renamed.equals(channelService.getChannelById(channelId).getChannelName()), "채널 이름 수정 실패");
            checkException("중복 채널 이름", "라는 채널이 이미 존재합니다.", () ->
                    channelService.createChannel(new Channel(general.getChannelName(), woody.getId())));
            checkException("방장이 아닌 채널 수정", "방장만", () ->
                    channelService.updateChannelName(channelId, buzz.getId(), "권한 없는 수정"));

            channelService.deleteChannel(channelId, woody.getId());
            ensure(!existsChannel(channelService, channelId), "채널이 삭제되지 않았습니다.");
            ensure(!userService.getUserById(woody.getId()).getMyChannelIds().contains(channelId),
                    "우디의 채널 목록에 삭제된 채널이 남아 있습니다.");
            ensure(!userService.getUserById(buzz.getId()).getMyChannelIds().contains(channelId),
                    "버즈의 채널 목록에 삭제된 채널이 남아 있습니다.");
            boolean channelMessagesRemain = messageService.getAllMessages().stream()
                    .anyMatch(message -> message.getChannelId().equals(channelId));
            ensure(!channelMessagesRemain, "삭제된 채널의 메시지가 남아 있습니다.");
            System.out.println("채널 이름 수정, 중복·권한 예외, 삭제와 관계 정리 확인");
        } finally {
            if (existsChannel(channelService, channelId)) {
                channelService.deleteChannel(channelId, woody.getId());
            }
        }
    }

    private static void printChannel(Channel channel, MessageService messageService, UserService userService) {
        System.out.println("# " + channel.getChannelName());
        for (Message message : messageService.getMessagesByChannelId(channel.getId())) {
            System.out.println(userService.getUserById(message.getSenderId()).getUsername()
                    + ": " + message.getContent());
        }
    }

    private static boolean existsChannel(ChannelService service, UUID id) {
        return service.getAllChannels().stream().anyMatch(saved -> saved.getId().equals(id));
    }

    private static void checkException(String name, String expected, Runnable action) {
        try {
            action.run();
            throw new IllegalStateException(name + " 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage() == null || !e.getMessage().contains(expected)) throw e;
            System.out.println(name + " 검증 성공: " + e.getMessage());
        }
    }

    private static void ensure(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
