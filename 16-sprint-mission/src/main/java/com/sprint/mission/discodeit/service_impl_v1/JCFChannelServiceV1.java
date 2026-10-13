package com.sprint.mission.discodeit.service_impl_v1;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.Map;

public class JCFChannelServiceV1 implements ChannelService {

    private final Map<String, Channel> data;

    public JCFChannelServiceV1() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(ChannelType channelType, String title, String description) {
        Channel newChannel = new Channel(channelType, title, description);
        data.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    @Override
    public Channel read(String id) throws Exception {
        Channel channel = data.get(id);
        if (channel == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
            throw new Exception("NPE 발생");
        }
        return channel;
    }

    @Override
    public void updateChannelType(String id, ChannelType newChannelType) throws Exception {
        Channel channel = read(id);
        channel.setChannelType(newChannelType);
    }

    @Override
    public void updateTitle(String id, String newTitle) throws Exception {
        Channel channel = read(id);
        channel.setTitle(newTitle);
    }

    @Override
    public void updateDescription(String id, String newDescription) throws Exception {
        Channel channel = read(id);
        channel.setDescription(newDescription);
    }

    @Override
    public Channel delete(String id) throws Exception {
        Channel removed = data.remove(id);
        if (removed == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
            throw new Exception("NPE 발생");
        }
        return removed;
    }
}
