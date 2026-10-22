package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        if (userService == null) throw new NullPointerException("유저 서비스가 참조하는 값이 없습니다.");
        if (channelService == null) throw new NullPointerException("채널 서비스가 참조하는 값이 없습니다.");

        this.data = new LinkedHashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(User sentFrom, String content, Channel channel) {
        if (sentFrom == null) throw new NullPointerException("메세지를 생성하는 유저가 참조하는 값이 없습니다.");
        if (channel == null) throw new NullPointerException("메세지를 생성할 채널이 참조하는 값이 없습니다.");

        // 유저와 채널 존재 여부 확인
        this.userService.findById(sentFrom.getId())
                .orElseThrow(() -> new NoSuchElementException("메세지를 보내는 유저가 존재하지 않습니다."));
        this.channelService.findById(channel.getId())
                .orElseThrow(() -> new NoSuchElementException("메세지를 보낼 채널이 존재하지 않습니다."));
        sentFrom.validateNotDeleted();
        channel.validateNotDeleted();

        // 채널 가입 여부 확인
        channel.getUsers()
                .stream()
                .filter(userId -> this.userService.findById(userId).isPresent())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("채널에 가입되지 않은 유저입니다."));

        Message message = new Message(sentFrom, content, channel);

        // 메세지 추가 시 유저와 채널에도 추가
        sentFrom.addMessage(message.getId());
        this.userService.update(sentFrom.getId(), sentFrom);

        channel.addMessage(message.getId());
        this.channelService.update(channel.getId(), channel);

        this.data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message update(UUID id, Message newMessage) {
        Message message = this.findById(id).orElseThrow(() -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다."));
        this.validateNotDeleted(message);
        message.update(newMessage);
        this.data.put(id, message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Message message = this.findById(id).orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 존재하지 않습니다."));
        this.validateNotDeleted(message);

        // 메세지 삭제 시 채널과 유저에도 메세지 삭제
        message.getChannel().removeMessage(id);
        message.getSentFrom().removeMessage(id);

        message.softDelete();
    }

    public void validateNotDeleted(Message message) {
        if (message.isDeleted()) throw new IllegalStateException("삭제된 메세지입니다.");
    }
}
