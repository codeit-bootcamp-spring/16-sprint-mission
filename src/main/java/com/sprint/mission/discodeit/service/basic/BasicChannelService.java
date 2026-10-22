package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Channel create(String name) {
        if(channelRepository.findAll().stream().anyMatch(c -> c.getName().equals(name))){
            throw new IllegalArgumentException("사용 중인 채널명");
        }

        Channel channel = new Channel(name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = findById(id);

        if(channelRepository.findAll().stream().anyMatch(otherChannel ->
                !otherChannel.getId().equals(id)&&otherChannel.getName().equals(name))){
            throw new IllegalArgumentException("사용 중인 채널명");
        }

        channel.update(name);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);

        for(User user : userRepository.findAll()){
            boolean removed = user.getChannels().removeIf(c -> c.getId().equals(id));
            if(removed){
                userRepository.save(user);
            }
        }

        channel.getUsers().clear();
        channelRepository.deleteById(id);
    }

    @Override
    public void join(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(channel.getUsers().stream().anyMatch(u -> u.getId().equals(userId))){
            throw new IllegalArgumentException("이미 참여한 사용자입니다");
        }

        channel.getUsers().add(user);

        if(user.getChannels().stream().noneMatch(c -> c.getId().equals(channelId))){
            user.getChannels().add(channel);
        }

        userRepository.save(user);
        channelRepository.save(channel);
    }
}