package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.data = new ArrayList<>();
    }


    @Override
    public Channel create(String name) {
        Channel  channel = new Channel(name);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.stream().filter(channel -> channel.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return data.stream().collect(Collectors.toList());
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = findById(id);
        if (channel == null) {
            return null;
        }
        channel.update(name);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);
        if (channel == null) {
            return;
        }
        for (User user : channel.getUsers()) {
            user.getChannels().remove(channel);
        }
        channel.getUsers().clear();
        data.remove(channel);

    }
    @Override
    public void join(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        if(user == null || channel == null){
            System.out.println("유저또는 채널이 유효하지않습니다!!");
            return;
        }
        if(channel.getUsers().contains(user)){
            System.out.println("중복입니다.");
            return;
        }
        channel.getUsers().add(user);
        user.getChannels().add(channel);
    }

}
