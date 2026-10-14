package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelsData = new HashMap<>();

    private UserService userService;
    private MessageService messageService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        Channel channel = channelsData.get(channelId);
        if (!channelsData.containsKey(channelId)) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> getChannelsByCreatorId(UUID creatorId) {
        return channelsData.values().stream()
                .filter(c -> c.getCreatorId().equals(creatorId))
                .toList();
    }

    @Override
    public void createChannel(Channel channel) {
        userService.getUserById(channel.getCreatorId());
        channelsData.put(channel.getId(), channel);
    }

    @Override
    public void updateChannelName(UUID channelId, String channelName) {
        Channel channel = getChannelById(channelId);
        channel.changeChannelName(channelName);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        getChannelById(channelId);

        // 해당 채널에 있는 메시지 먼저 다 삭제
        List<Message> messageList = messageService.getMessagesByChannelId(channelId);
        messageList.stream()
                        .map(Message::getId)
                        .forEach(messageId -> messageService.deleteMessage(messageId));

        // 이후 채널 삭제
        channelsData.remove(channelId);
    }
}
