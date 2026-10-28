package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Channel create(String channelName, String topic, String type) {
        Channel channel = new Channel(channelName, topic, type);
        return channelRepository.save(channel);
    }

    @Override
    public Channel read(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> readAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String channelName, String topic, String type) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("사용하지 않는 채널입니다: " + id);
        }
        channel.update(channelName, topic, type);
        return channelRepository.save(channel);
    }

    @Override
    public void addParticipant(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        if(channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다:" + channelId);
        }
        if(userRepository.findById(userId) == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다:" + userId);
        }
        channel.addParticipant(userId);
        channelRepository.save(channel);
    }

    @Override
    public void removeParticipant(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        if(channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다:" + channelId);
        }
        if(userRepository.findById(userId) == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다:" + userId);
        }
        channel.removeParticipant(userId);
        channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        channelRepository.deleteById(id);
    }
}
