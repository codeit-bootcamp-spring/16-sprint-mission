package com.sprint.mission.discodeit.service.service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    // Channel에 참여중인 User들을 리스트로 반환
    public List<User> getJoiningUsers(Channel channelUUID, UserService userService) {
        Channel channel = data.get(channelUUID);
        List<User> joiningChannels = userService.getChannelsFromUser(channel);
        return joiningChannels;
    }

    // JCFUserService에서 사용
    @Override
    public List<Channel> getUserChannels(User user) {
        List<UUID> channelUUIDs = user.getChannelUUIDs();
        List<Channel> channels = new ArrayList<>();

        for (UUID channelUUID : channelUUIDs) {
            Channel channel = data.get(channelUUID);
            if (channel != null) {
                channels.add(channel);
            }
        }
        return channels;
    }

    @Override
    public Channel create(User user, ChannelType channelType, String title, String description) {
        Channel newChannel = new Channel(channelType, title, description);
        data.put(newChannel.getId(), newChannel);
        user.addChannel(newChannel);
        return newChannel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    public List<Optional<Channel>> findAll(User user) {
        List<UUID> channelUUIDs = user.getChannelUUIDs();
        List<Optional<Channel>> channels = new ArrayList<>();

        for (UUID channelUUID : channelUUIDs) {
            channels.add(findById(channelUUID));
        }
        return channels;
    }

    @Override
    public Optional<Channel> updateChannelType(UUID id, ChannelType newChannelType) {
        Optional<Channel> channel = findById(id);
        if (channel.isEmpty()) {
            System.out.println("존재하지 않는 채널의 타입을 바꿀 수 없습니다.");
            return channel;
        }
        channel.get().setChannelType(newChannelType);
        return channel;
    }

    @Override
    public Optional<Channel> updateTitle(UUID id, String newTitle) {
        Optional<Channel> channel = findById(id);
        if (channel.isEmpty()) {
            System.out.println("존재하지 않는 채널의 제목을 바꿀 수 없습니다.");
            return channel;
        }
        channel.get().setTitle(newTitle);
        return channel;
    }

    @Override
    public Optional<Channel> updateDescription(UUID id, String newDescription) {
        Optional<Channel> channel = findById(id);
        if (channel.isEmpty()) {
            System.out.println("존재하지 않는 채널의 설명을 바꿀 수 없습니다.");
            return channel;
        }
        channel.get().setDescription(newDescription);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Channel removed = data.remove(id);
        if (removed == null) {
            throw new NoSuchElementException();
        }
    }
}
