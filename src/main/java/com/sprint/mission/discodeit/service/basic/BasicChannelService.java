package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel add(Channel channel) {
        if (channelRepository.getById(channel.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }

        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel getById(UUID id) {
        return channelRepository.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> getAll() {
        return channelRepository.getAll();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = getById(id);
        channel.update(name);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        channelRepository.delete(id);
    }
}