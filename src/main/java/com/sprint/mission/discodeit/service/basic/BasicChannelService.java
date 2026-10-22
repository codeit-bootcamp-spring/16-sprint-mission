package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(
            ChannelRepository channelRepository,
            UserRepository userRepository,
            MessageRepository messageRepository
    ) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    private void validateDuplicateChannelName(String channelName, UUID excludedId) {
        boolean exists = channelRepository.findAll().stream()
                .anyMatch(channel -> channel.getChannelName().equals(channelName)
                        && !channel.getId().equals(excludedId));
        if (exists) {
            throw new IllegalArgumentException("\"" + channelName + "\" 라는 채널이 이미 존재합니다.");
        }
    }

    private void validateCreator(Channel channel, UUID creatorId, String action) {
        if (!channel.getCreator().getId().equals(creatorId)) {
            throw new IllegalArgumentException("방장만 채널을 " + action + "할 수 있습니다.");
        }
    }

    private Channel requireChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
        return channel;
    }

    private User requireUser(UUID userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    private void postSystemMessage(User user, Channel channel, String content) {
        Message message = new Message(content, user, channel);
        user.addMessage(message);
        channel.addChannelMessages(message);
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

        User creator = requireUser(channel.getCreator().getId());
        validateDuplicateChannelName(channel.getChannelName(), null);

        channel.joinChannel(creator);
        userRepository.save(creator);
        channelRepository.save(channel);
        postSystemMessage(
                creator,
                channel,
                "[" + channel.getChannelName() + "] 채널이 생성되었습니다."
        );
        return channel;
    }

    @Override
    public Channel joinChannel(UUID channelId, UUID userId) {
        User user = requireUser(userId);
        Channel channel = requireChannel(channelId);

        channel.joinChannel(user);
        userRepository.save(user);
        channelRepository.save(channel);
        postSystemMessage(
                user,
                channel,
                user.getUsername() + "님이 입장하셨습니다."
        );
        return channel;
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        User user = requireUser(userId);
        Channel channel = requireChannel(channelId);

        if (!isParticipant(channelId, userId)) {
            throw new IllegalArgumentException("참여 중인 채널이 아닙니다.");
        }
        if (channel.getCreator().getId().equals(userId)) {
            throw new IllegalArgumentException("방장은 채널을 나갈 수 없습니다. 채널을 삭제해 주세요.");
        }

        postSystemMessage(
                user,
                channel,
                user.getUsername() + "님이 퇴장하셨습니다."
        );
        channel.leaveChannel(user);
        userRepository.save(user);
        channelRepository.save(channel);
    }

    @Override
    public boolean isParticipant(UUID channelId, UUID userId) {
        requireUser(userId);
        Channel channel = requireChannel(channelId);
        return channel.getChannelParticipants().stream()
                .anyMatch(user -> user.getId().equals(userId));
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
        return requireChannel(channelId).getChannelParticipants();
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
        Channel channel = requireChannel(channelId);
        validateCreator(channel, userId, "삭제");

        for (Message message : messageRepository.findByChannelId(channelId)) {
            User sender = userRepository.findById(message.getSender().getId());
            if (sender != null) {
                sender.removeMessage(message);
                userRepository.save(sender);
            }
            channel.removeChannelMessages(message);
            messageRepository.deleteById(message.getId());
        }

        for (User participant : channel.getChannelParticipants()) {
            channel.leaveChannel(participant);
            User savedParticipant = userRepository.findById(participant.getId());
            if (savedParticipant != null) {
                savedParticipant.removeChannel(channel);
                userRepository.save(savedParticipant);
            }
        }

        channelRepository.deleteById(channelId);
    }
}
