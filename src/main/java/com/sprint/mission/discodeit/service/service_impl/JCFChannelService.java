package com.sprint.mission.discodeit.service.service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;
    private final UserService userService;
    private final MessageService messageService;

    public JCFChannelService(UserService userService, MessageService messageService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public Channel create(User user, ChannelType channelType, String title, String description) {
        // userService에 등록된 User만 Channel을 생성할 수 있음
        if (userService.findById(user.getId()).isEmpty()) {
            throw new NoSuchElementException("등록되지 않은 User는 Channel을 생성할 수 없습니다.");
        }

        Channel newChannel = new Channel(channelType, title, description);
        data.put(newChannel.getId(), newChannel);
        user.addChannel(newChannel);
        newChannel.addUser(user);
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
        Channel removed = data.get(id);
        if (removed == null) {
            throw new NoSuchElementException();
        }

        // Channel에 속한 Message 삭제 (작성한 User의 myMessages도 함께 정리됨)
        // messageService.delete()가 channelMessages를 수정하므로 복사본으로 순회
        for (Message message : new ArrayList<>(removed.getChannelMessages())) {
            messageService.delete(message.getId());
        }

        // 삭제된 Channel에 속해있던 User들에게서도 Channel 정보 삭제
        for (User user : removed.getUsers()) {
            user.deleteChannel(removed);
        }

        data.remove(id);
    }
}
