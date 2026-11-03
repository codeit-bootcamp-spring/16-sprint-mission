package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.IntegrateChatService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class JCFIntegrateChatService implements IntegrateChatService {
    final UserService userService;
    final ChannelService channelService;
    final MessageService messageService;
    public JCFIntegrateChatService(UserService userService, ChannelService channelService, MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    public void joinChannel(UUID userId, UUID channelId) {
        User user = userService.findEntityId(userId);
        Channel channel = channelService.findEntityChannel(channelId);
        if (user == null || channel == null) throw new NullPointerException("유저 또는 채널이 존재하지 않습니다.");

        user.joinChannel(channelId);
        channel.addUser(userId);

        userService.save(user);
        channelService.save(channel);
    }

    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userService.findEntityId(userId);
        Channel channel = channelService.findEntityChannel(channelId);
        if (user == null || channel == null) throw new NullPointerException("유저 또는 채널이 존재하지 않습니다.");

        user.leaveChannel(channelId);
        channel.removeUser(userId);

        userService.save(user);
        channelService.save(channel);
    }

    public void deleteUserCompletely(UUID userId) {
        User user = userService.findEntityId(userId);
        if (user == null) return;


        for (UUID channelId : user.getChannels()) {
            Channel channel = channelService.findEntityChannel(channelId);
            if (channel != null) {
                channel.removeUser(userId);
                channelService.save(channel);
            }
        }
        messageService.deleteByUserId(userId);
        userService.deleteId(userId);
    }

    public void deleteChannelCompletely(UUID channelId) {
        Channel channel = channelService.findEntityChannel(channelId);
        if (channel == null) return;

        for (UUID userId : channel.getUserId()) {
            User user = userService.findEntityId(userId);
            if (user != null) {
                user.leaveChannel(channelId);
                userService.save(user);
            }
        }
        messageService.deleteByChannelId(channelId);
        channelService.deleteById(channelId);
    }
}
