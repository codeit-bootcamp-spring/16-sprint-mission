package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelService implements ChannelService {
    final Map<UUID, Channel> data;
    final MessageService messageService;

    public JCFChannelService(MessageService messageService) {
        this.data = new ConcurrentHashMap<>();
        this.messageService = messageService;
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null || channel.getId() == null) {
            throw new NullPointerException("채널 데이터 오류.");
        }
        data.put(channel.getId(), channel);
        return channel;
    }


    @Override
    public List<Channel> findId(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            return Collections.emptyList();
        }
        List<Channel> result = new ArrayList<>();
        result.add(channel);
        return result;
    }
    @Override
    public Channel findEntityChannel (UUID id) {
        return data.get(id); // Map에서 채널 객체 직접 반환
    }
    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return messageService.findChannelId(channelId); // messageService 기능 활용
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new NullPointerException("해당 채널이 없습니다. ID: " + id);
        }
        channel.setName(name);
        return channel;
    }

    @Override
    public void deleteById(UUID id) {
        messageService.deleteByChannelId(id); // 채널메시지까지 삭제
        data.remove(id);
    }
}
