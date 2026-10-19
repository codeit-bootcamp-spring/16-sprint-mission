package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserWithdrawalService;

import java.util.ArrayList;
import java.util.UUID;

public class JCFUserWithdrawalService implements UserWithdrawalService {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public JCFUserWithdrawalService(UserService userService, ChannelService channelService, MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    @Override
    public void withdraw(UUID userId) {
        User user = userService.findById(userId);

        // 검증: 소유한 채널이 있다면 유저 정보 삭제가 불가(소유권 양도가 필요)
        for (UUID channelId : user.getChannels()) {
            if (channelService.findById(channelId).isOwner(userId)) {
                throw new IllegalArgumentException("소유한 채널이 있어 탈퇴할 수 없습니다.");
            }
        }

        // 메시지 삭제: 유저 삭제 시 작성한 유저의 메시지함 속 메시지도 함께 삭제
        for (UUID messageId : new ArrayList<>(user.getMessages())) {
            messageService.delete(messageId, userId);
        }

        // 채널 퇴장: 유저 삭제 시 참여 중인 채널에서 퇴장됨
        for (UUID channelId : new ArrayList<>(user.getChannels())) {
            channelService.leave(channelId, userId);
        }

        userService.delete(userId);
    }
}
