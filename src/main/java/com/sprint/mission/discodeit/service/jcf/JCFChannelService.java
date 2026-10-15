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
    final UserService userService;

    public JCFChannelService(MessageService messageService , UserService userService) {
        this.data = new ConcurrentHashMap<>();
        this.messageService = messageService;
        this.userService=userService;
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
    public void joinChannel(UUID userId, UUID channelId) {
        User user = userService.findEntityId(userId);
        Channel channel = data.get(channelId);

        if (user == null || channel == null) throw new NullPointerException("유저/채널 없음");

        user.joinChannel(channelId);
        channel.addUser(userId);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userService.findEntityId(userId);
        Channel channel = data.get(channelId);

        if (user == null || channel == null) throw new NullPointerException("유저/채널 없음");

        user.leaveChannel(channelId);
        channel.removeUser(userId);
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
        Channel channel = data.get(id);
        if (channel != null) {
            for (UUID userId : channel.getUserId()) {
                User user = userService.findEntityId(userId);
                if (user != null) user.leaveChannel(id);
            }
        }
        messageService.deleteByChannelId(id); // 채널메시지까지 삭제
        data.remove(id);
    }
}
