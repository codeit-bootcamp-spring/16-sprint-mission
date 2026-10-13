package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.BaseEntity;
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

    private void validateChannelId(UUID channelId) {
        if (!channelsData.containsKey(channelId)) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
    }

    @Override
    public boolean createChannel(Channel channel) {
        userService.getUserById(channel.getCreatorId());

        channelsData.put(channel.getId(), channel);
        return true;
    }

    @Override
    public boolean updateChannelName(UUID channelId, String channelName) {
        Channel channel = channelsData.get(channelId);
        channel.changeChannelName(channelName);
        return true;
    }

    @Override
    public boolean deleteChannel(UUID channelId) {
        validateChannelId(channelId);

        // 해당 채널에 있는 메시지 다 삭제
        List<Message> messageList = messageService.getMessagesByChannelId(channelId);
        messageList.stream()
                        .map(BaseEntity::getId)
                        .forEach(id -> messageService.deleteMessage(id));

        channelsData.remove(channelId);
        return true;
    }

    @Override
    public void getChannelById(UUID channelId) {
        validateChannelId(channelId);

        channelsData.get(channelId);
    }

    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        return channelsData.values().stream()
                .filter(c -> c.getCreatorId().equals(userId))
                .toList();
    }

}
