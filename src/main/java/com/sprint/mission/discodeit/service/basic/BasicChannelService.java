package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
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

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Channel create(String name) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<Channel> channels = entities.getChannels();

        if(channels.stream().anyMatch(c -> c.getName().equals(name))){
            throw new IllegalArgumentException("사용 중인 채널명");
        }

        Channel channel = new Channel(name);
        channels.add(channel);
        entities.saveAll();
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        return entities.getChannels().stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));
    }

    @Override
    public List<Channel> findAll() {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        return entities.getChannels();
    }

    @Override
    public Channel update(UUID id, String name) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<Channel> channels = entities.getChannels();

        Channel channel = channels.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        if(channels.stream().anyMatch(otherChannel ->
                !otherChannel.getId().equals(id)&&otherChannel.getName().equals(name))){
            throw new IllegalArgumentException("사용 중인 채널명");
        }

        channel.update(name);
        entities.saveAll();
        return channel;
    }

    @Override
    public void delete(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<Channel> channels = entities.getChannels();

        Channel channel = channels.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        channels.remove(channel);
        entities.saveAll();
    }

    @Override
    public void join(UUID channelId, UUID userId) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        Channel channel = entities.getChannels().stream().filter(c -> c.getId().equals(channelId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        User user = entities.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(channel.getUsers().stream().anyMatch(u -> u.getId().equals(userId))){
            throw new IllegalArgumentException("이미 참여한 사용자입니다");
        }

        channel.getUsers().add(user);
        user.getChannels().add(channel);
        entities.saveAll();
    }
}