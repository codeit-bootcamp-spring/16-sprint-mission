package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserService userService;
    private MessageService messageService;

    public BasicChannelService(
            ChannelRepository channelRepository,
            UserService userService
    ) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel create(String name, String description) {
        validateChannel(name, description);

        boolean nameExists = channelRepository.findAll().stream()
                .anyMatch(channel -> channel.getName().equals(name));

        if (nameExists) {
            throw new IllegalArgumentException("이미 사용 중인 채널 이름입니다.");
        }

        return save(new Channel(name, description));
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        validateChannel(name, description);

        boolean nameExists = channelRepository.findAll().stream()
                .anyMatch(existing ->
                        !existing.getId().equals(id)
                                && existing.getName().equals(name));

        if (nameExists) {
            throw new IllegalArgumentException("이미 사용 중인 채널 이름입니다.");
        }

        channel.update(name, description);
        return save(channel);
    }

    private void validateChannel(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("채널 설명은 필수입니다.");
        }
    }

    private Channel save(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널 정보는 필수입니다.");
        }

        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (messageService != null) {
            for (Message message : new ArrayList<>(messageService.findAll())) {
                if (message.getChannelId().equals(id)) {
                    messageService.delete(message.getId());
                }
            }
        }

        for (UUID userId : new ArrayList<>(channel.getMemberIds())) {
            userService.removeChannel(userId, id);
        }

        channelRepository.deleteById(id);
    }

    @Override
    public void join(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        userService.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (channel.hasMember(userId)) {
            throw new IllegalArgumentException("이미 참여 중인 채널입니다.");
        }

        channel.addMember(userId);
        save(channel);
        userService.addChannel(userId, channelId);
    }

    @Override
    public void leave(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        userService.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!channel.hasMember(userId)) {
            throw new IllegalArgumentException(
                    "참여하지 않은 채널에서는 나갈 수 없습니다.");
        }

        channel.removeMember(userId);
        save(channel);
        userService.removeChannel(userId, channelId);
    }
}