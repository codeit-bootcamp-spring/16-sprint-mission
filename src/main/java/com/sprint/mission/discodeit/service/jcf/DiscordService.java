package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class DiscordService {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public DiscordService(UserService userService,
                          ChannelService channelService,
                          MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    public void deleteUser(UUID userId) {
        // 먼저 존재하는 유저인지 확인
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );
        
        // 해당 유저가 가지고 있는 채팅방 리스트
        List<Channel> channels = channelService.findAllByUserId(userId);

        for(Channel channel : channels) {
            if (channel.getUsers().size() == 1) { // 남은 마지막 유저라면
                // 채널, 유저, 메세지 전부 삭제하면 됨
                // 메시지 -> 채널 순으로 삭제
                messageService.deleteByChannelId(channel.getId()); // 채팅방 메세지 전체 삭제
                channelService.deleteChannel(channel.getId()); // 채널 삭제
            } else {
                // 채널에서 해당하는 유저만 삭제하기
                channelService.removeUserFromChannel(channel.getId(), List.of(userId));
            }
        }
        messageService.deleteByUserId(userId); // 혹시 모르니까 삭제할 유저의 모든 메세지 삭제
        userService.deleteUser(userId); // 최종적으로 유저를 삭제하기
    }
}
