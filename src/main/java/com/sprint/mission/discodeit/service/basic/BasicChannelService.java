package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

/** 채널 업무 흐름을 처리하고 영속화는 ChannelRepository에 맡깁니다. */
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    /** 생성자에 받은 인터페이스 구현체가 실제 저장 방식을 결정합니다. */
    public BasicChannelService(ChannelRepository channelRepository) {
        if (channelRepository == null) {
            throw new IllegalArgumentException("ChannelRepository cannot be null.");
        }
        this.channelRepository = channelRepository;
    }

    @Override
    public void create(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null.");
        }
        if (channelRepository.existsById(channel.getId())) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + channel.getId());
        }

        channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found channel id: " + id));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void update(UUID id, Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null.");
        }

        Channel existingChannel = findById(id);
        existingChannel.update(channel.getName(), channel.getDescription());
        channelRepository.save(existingChannel);
    }

    @Override
    public void delete(UUID id) {
        if (!channelRepository.deleteById(id)) {
            throw new IllegalArgumentException("Not Found channel id: " + id);
        }
    }
}
