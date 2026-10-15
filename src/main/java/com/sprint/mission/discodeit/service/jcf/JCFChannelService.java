package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

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

    // 채널 이름 중복 검증
    private void validateDuplicateChannelName(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("채널명은 null이거나 공백일 수 없습니다.");
        }
        boolean exists = channelsData.values().stream()
                .anyMatch(c -> channelName.equals(c.getChannelName()));
        if (exists) {
            throw new RuntimeException("\"" + channelName + "\" 라는 채널이 이미 존재합니다.");
        }
    }

    // 채널 생성자(방장) 검증
    private void validateCreator(Channel channel, UUID creatorId, String action) {
        if (!channel.getCreatorId().equals(creatorId)) {
            throw new IllegalArgumentException("방장만 채널을 " + action +"할 수 있습니다.");
        }
    }

    // 생성
    @Override
    public Channel createChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 null입니다.");
        }
        if (channelsData.containsKey(channel.getId())) {
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }
        User user = userService.getUserById(channel.getCreatorId());
        validateDuplicateChannelName(channel.getChannelName());

        channelsData.put(channel.getId(), channel);
        joinChannel(channel.getId(), user.getId());

        return channel;
    }

    // 참여/퇴장
    @Override
    public Channel joinChannel(UUID channelId, UUID userId) {
        User user = userService.getUserById(userId);
        Channel channel = getChannelById(channelId);

        channel.joinChannel(user);

        Message message = new Message(
                user.getUsername() + "님이 입장하셨습니다.", userId, channelId);
        messageService.createMessage(message);

        return channel;
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        User user = userService.getUserById(userId);
        Channel channel = getChannelById(channelId);

        if (!isParticipant(channelId, userId)) {
            throw new IllegalArgumentException("참여 중인 채널이 아닙니다.");
        }
        if (channel.getCreatorId().equals(user.getId())) {
            throw new IllegalArgumentException("방장은 채널을 나갈 수 없습니다. 채널을 삭제해 주세요.");
        }

        Message message = new Message(
                user.getUsername() + "님이 퇴장하셨습니다.", userId, channelId);
        messageService.createMessage(message);

        channel.leaveChannel(user);
    }

    @Override
    public boolean isParticipant(UUID channelId, UUID userId) {
        userService.getUserById(userId);
        Channel channel = getChannelById(channelId);

        return channel.getChannelParticipants().stream()
                .anyMatch(u -> u.getId().equals(userId));
    }

    // 조회
    @Override
    public Channel getChannelById(UUID channelId) {
        Channel channel = channelsData.get(channelId);
        if (!channelsData.containsKey(channelId)) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelsData.values().stream()
                .toList();
    }

    @Override
    public List<Channel> getChannelsByCreatorId(UUID creatorId) {
        userService.getUserById(creatorId);

        return channelsData.values().stream()
                .filter(c -> c.getCreatorId().equals(creatorId))
                .toList();
    }

    @Override
    public List<Channel> getChannelsByParticipantId(UUID participantId) {
        userService.getUserById(participantId);

        return channelsData.values().stream()
                .filter(c -> c.getChannelParticipants().stream()
                        .anyMatch(u -> u.getId().equals(participantId)))
                .toList();
    }

    @Override
    public List<User> getParticipants(UUID channelId) {
        Channel channel = getChannelById(channelId);

        return channel.getChannelParticipants();
    }

    // 수정
    @Override
    public Channel updateChannelName(UUID channelId, UUID userId, String channelName) {
        Channel channel = getChannelById(channelId);
        validateCreator(channel, userId, "수정");
        validateDuplicateChannelName(channelName);

        channel.changeChannelName(channelName);
        return channel;
    }

    // 삭제
    @Override
    public void deleteChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelById(channelId);
        validateCreator(channel, userId, "삭제");

        messageService.deleteMessagesByChannelId(channelId);

        List<User> participants = new ArrayList<>(getParticipants(channelId));
        participants.forEach(user -> user.removeChannel(channel));

        channelsData.remove(channelId);
    }
}
