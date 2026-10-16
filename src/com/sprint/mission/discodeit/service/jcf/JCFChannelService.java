package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;
    private final UserService userService;
    private MessageService messageService;

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String name, String description) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("채널 설명은 필수입니다.");
        }

        // 같은 이름의 채널이 있는지 확인
        boolean nameExists = data.values()
                .stream()
                .anyMatch(channel ->
                        channel.getName().equals(name));

        if (nameExists) {
            throw new IllegalArgumentException(
                    "이미 존재하는 채널 이름입니다."
            );
        }

        // 중복이 없으면 채널 생성
        Channel channel = new Channel(name, description);
        data.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("채널 설명은 필수입니다.");
        }

        // 현재 채널을 제외하고 같은 이름이 있는지 확인
        boolean nameExists = data.values()
                .stream()
                .anyMatch(existingChannel ->
                        !existingChannel.getId().equals(id)
                                && existingChannel.getName().equals(name));
        if (nameExists) {
            throw new IllegalArgumentException(
                    "이미 존재하는 채널 이름입니다."
            );
        }
        channel.update(name, description);
        return channel;
    }


    @Override
    public void delete(UUID id) {
        // 1. 삭제할 채널 조회
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        // 2. 채널에 연결된 메시지를 실제 메시지 저장소에서도 삭제
        for (Message message : new java.util.ArrayList<>(channel.getMessages())) {
            messageService.delete(message.getId());
        }

        // 3. 참여 사용자와의 관계 제거
        for (User user : new java.util.ArrayList<>(channel.getUsers())) {
            user.removeChannel(channel);
            channel.removeUser(user);
        }

        // 4. 채널 저장소에서 제거
        data.remove(id);
    }


    @Override
    public void join(UUID channelId, UUID userId) {
        Channel channel = findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        User user = userService.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (channel.hasUser(user)) {
            throw new IllegalArgumentException("이미 참여중인 채널입니다.");
        }

        channel.addUser(user);
        user.addChannel(channel);
    }

    @Override
    public void leave(UUID channelId, UUID userId) {
        Channel channel = findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        User user = userService.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!channel.hasUser(user)) {
            throw new IllegalArgumentException("참여하지 않은 채널입니다.");
        }

        channel.removeUser(user);
        user.removeChannel(channel);
    }
}