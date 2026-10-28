package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;
    private final UserRepository userRepository;
    private final ChannelRepository  channelRepository;

    public JCFChannelService(UserService userService, UserRepository userRepository, ChannelRepository channelRepository) {
        this.data = new ArrayList<>();
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("Channel Name cannot be null or empty");
        }

        Channel channel = new Channel(channelName);
        // this.data.add(channel);
        this.channelRepository.save(channel);
        return channel;
    }

    @Override
    public List<Channel> findAll() {

        List<Channel> channelList = this.channelRepository.getChannels();
        return channelList;
    }

    @Override
    public Channel findById(UUID id) {

        Channel channel = this.channelRepository.getChannel(id);

        return channel;
    }

    @Override
    public Channel updateChannelName(UUID id, String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("Channel Name cannot be null or empty");
        }

        Channel channel = this.findById(id);
        channel.updateChannelName(channelName);

        return this.channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID id) {
        try {
            this.channelRepository.removeChannel(id);
        } catch (NoSuchElementException exception) {
            throw new NoSuchElementException("Channel with id " + id + " does not exist");
        }
    }

    @Override
    public void joinChannel(UUID id, UUID userId) {
        Channel channel = this.findById(id);
        User user = this.userRepository.getUser(userId);

        channel.addUser(user);
        user.addChannel(channel);
    }

    @Override
    public void leaveChannel(UUID id, UUID userId) {
        Channel channel = this.findById(id);
        User user = this.userRepository.getUser(userId);

        channel.removeUser(user);
        user.removeChannel(channel);
    }
}
