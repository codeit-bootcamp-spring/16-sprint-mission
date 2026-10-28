package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
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

    private void validateDuplicateChannelName(String name, UUID excludedId) {
        if (channelsData.values().stream().anyMatch(c -> c.getChannelName().equals(name)
                && !c.getId().equals(excludedId))) {
            throw new IllegalArgumentException("\"" + name + "\" 라는 채널이 이미 존재합니다.");
        }
    }

    private void validateCreator(Channel channel, UUID userId, String action) {
        if (!channel.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("방장만 채널을 " + action + "할 수 있습니다.");
        }
    }

    private void postSystemMessage(UUID userId, UUID channelId, String content) {
        messageService.createMessage(new Message(content, userId, channelId));
    }

    @Override
    public Channel createChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 null입니다.");
        }
        if (channelsData.containsKey(channel.getId())) {
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }
        User creator = userService.getUserById(channel.getCreatorId());
        validateDuplicateChannelName(channel.getChannelName(), null);
        channel.joinChannel(creator.getId());
        creator.addChannelId(channel.getId());
        channelsData.put(channel.getId(), channel);
        postSystemMessage(creator.getId(), channel.getId(), "[" + channel.getChannelName() + "] 채널이 생성되었습니다.");
        return channel;
    }

    @Override
    public Channel joinChannel(UUID channelId, UUID userId) {
        User user = userService.getUserById(userId);
        Channel channel = getChannelById(channelId);
        channel.joinChannel(userId);
        user.addChannelId(channelId);
        postSystemMessage(userId, channelId, user.getUsername() + "님이 입장하셨습니다.");
        return channel;
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        User user = userService.getUserById(userId);
        Channel channel = getChannelById(channelId);
        if (!channel.getParticipantIds().contains(userId)) {
            throw new IllegalArgumentException("참여 중인 채널이 아닙니다.");
        }
        if (channel.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("방장은 채널을 나갈 수 없습니다. 채널을 삭제해 주세요.");
        }
        postSystemMessage(userId, channelId, user.getUsername() + "님이 퇴장하셨습니다.");
        channel.leaveChannel(userId);
        user.removeChannelId(channelId);
    }

    @Override
    public boolean isParticipant(UUID channelId, UUID userId) {
        userService.getUserById(userId);
        return getChannelById(channelId).getParticipantIds().contains(userId);
    }

    @Override
    public Channel getChannelById(UUID id) {
        Channel channel = channelsData.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(channelsData.values());
    }

    @Override
    public List<Channel> getChannelsByCreatorId(UUID creatorId) {
        userService.getUserById(creatorId);
        return channelsData.values().stream()
                .filter(channel -> channel.getCreatorId().equals(creatorId))
                .toList();
    }

    @Override
    public List<Channel> getChannelsByParticipantId(UUID userId) {
        userService.getUserById(userId);
        return channelsData.values().stream()
                .filter(channel -> channel.getParticipantIds().contains(userId))
                .toList();
    }

    @Override
    public List<User> getParticipants(UUID channelId) {
        List<User> result = new ArrayList<>();
        for (UUID userId : getChannelById(channelId).getParticipantIds()) {
            result.add(userService.getUserById(userId));
        }
        return List.copyOf(result);
    }

    @Override
    public Channel updateChannelName(UUID channelId, UUID userId, String name) {
        userService.getUserById(userId);
        Channel channel = getChannelById(channelId);
        validateCreator(channel, userId, "수정");
        validateDuplicateChannelName(name, channelId);
        channel.changeChannelName(name);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelById(channelId);
        validateCreator(channel, userId, "삭제");
        messageService.deleteMessagesByChannelId(channelId);
        for (UUID participantId : channel.getParticipantIds()) {
            channel.leaveChannel(participantId);
            userService.getUserById(participantId).removeChannelId(channelId);
        }
        channelsData.remove(channelId);
    }
}
