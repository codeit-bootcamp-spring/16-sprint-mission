package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private void validateDuplicateChannelName(String name, UUID excludedId) {
        boolean exists = channelRepository.findAll().stream()
                .anyMatch(channel -> channel.getChannelName().equals(name)
                        && !channel.getId().equals(excludedId));
        if (exists) {
            throw new IllegalArgumentException("\"" + name + "\" 라는 채널이 이미 존재합니다.");
        }
    }

    private void validateCreator(Channel channel, UUID creatorId, String action) {
        if (!channel.getCreatorId().equals(creatorId)) {
            throw new IllegalArgumentException("방장만 채널을 " + action + "할 수 있습니다.");
        }
    }

    private Channel requireChannel(UUID id) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
        return channel;
    }

    private User requireUser(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    private void postSystemMessage(UUID userId, UUID channelId, String content) {
        Message message = new Message(content, userId, channelId);
        User user = requireUser(userId);
        Channel channel = requireChannel(channelId);

        user.addMessageId(message.getId());
        channel.addMessageId(message.getId());

        userRepository.save(user);
        channelRepository.save(channel);
        messageRepository.save(message);
    }

    @Override
    public Channel createChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 null입니다.");
        }

        if (channelRepository.findById(channel.getId()) != null) {
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }

        User creator = requireUser(channel.getCreatorId());
        validateDuplicateChannelName(channel.getChannelName(), null);

        channel.joinChannel(creator.getId());
        creator.addChannelId(channel.getId());

        userRepository.save(creator);
        channelRepository.save(channel);

        postSystemMessage(creator.getId(), channel.getId(),
                "[" + channel.getChannelName() + "] 채널이 생성되었습니다.");

        return requireChannel(channel.getId());
    }

    @Override
    public Channel joinChannel(UUID channelId, UUID userId) {
        User user = requireUser(userId);
        Channel channel = requireChannel(channelId);

        boolean userHasChannel = user.getMyChannelIds().contains(channelId);
        boolean channelHasUser = channel.getParticipantIds().contains(userId);

        if (userHasChannel != channelHasUser) {
            throw new IllegalStateException("사용자와 채널의 참여 정보가 일치하지 않습니다.");
        }
        if (userHasChannel) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 유저입니다.");
        }

        channel.joinChannel(userId);
        user.addChannelId(channelId);

        userRepository.save(user);
        channelRepository.save(channel);

        postSystemMessage(userId, channelId, user.getUsername() + "님이 입장하셨습니다.");
        return requireChannel(channelId);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        User user = requireUser(userId);
        Channel channel = requireChannel(channelId);

        if (!channel.getParticipantIds().contains(userId)) {
            throw new IllegalArgumentException("참여 중인 채널이 아닙니다.");
        }
        if (channel.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("방장은 채널을 나갈 수 없습니다. 채널을 삭제해 주세요.");
        }

        postSystemMessage(userId, channelId, user.getUsername() + "님이 퇴장하셨습니다.");

        User latestUser = requireUser(userId);
        Channel latestChannel = requireChannel(channelId);

        latestChannel.leaveChannel(userId);
        latestUser.removeChannelId(channelId);

        userRepository.save(latestUser);
        channelRepository.save(latestChannel);
    }

    @Override
    public boolean isParticipant(UUID channelId, UUID userId) {
        requireUser(userId);
        return requireChannel(channelId).getParticipantIds().contains(userId);
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        return requireChannel(channelId);
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> getChannelsByCreatorId(UUID creatorId) {
        requireUser(creatorId);
        return channelRepository.findByCreatorId(creatorId);
    }

    @Override
    public List<Channel> getChannelsByParticipantId(UUID participantId) {
        requireUser(participantId);
        return channelRepository.findByParticipantId(participantId);
    }

    @Override
    public List<User> getParticipants(UUID channelId) {
        List<User> participants = new ArrayList<>();

        for (UUID participantId : requireChannel(channelId).getParticipantIds()) {
            User participant = userRepository.findById(participantId);

            if (participant != null) {
                participants.add(participant);
            }
        }

        return List.copyOf(participants);
    }

    @Override
    public Channel updateChannelName(UUID channelId, UUID userId, String channelName) {
        requireUser(userId);
        Channel channel = requireChannel(channelId);
        validateCreator(channel, userId, "수정");
        validateDuplicateChannelName(channelName, channelId);
        channel.changeChannelName(channelName);
        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID channelId, UUID userId) {
        requireUser(userId);

        Channel channel = requireChannel(channelId);
        validateCreator(channel, userId, "삭제");

        for (Message message : messageRepository.findByChannelId(channelId)) {
            User sender = userRepository.findById(message.getSenderId());
            if (sender != null) {
                sender.removeMessageId(message.getId());
                userRepository.save(sender);
            }

            channel.removeMessageId(message.getId());
            messageRepository.deleteById(message.getId());
        }

        for (UUID participantId : channel.getParticipantIds()) {
            channel.leaveChannel(participantId);

            User participant = userRepository.findById(participantId);
            if (participant != null) {
                participant.removeChannelId(channelId);
                userRepository.save(participant);
            }
        }

        channelRepository.deleteById(channelId);
    }
}
