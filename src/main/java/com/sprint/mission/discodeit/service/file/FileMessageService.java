package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        this.writeMessageFile(new HashMap<>());
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
        channel.getUsers().values()
                .stream()
                .filter(user -> user.getId().equals(sentFrom.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("채널에 가입되지 않은 유저입니다."));

        Message message = new Message(sentFrom, content, channel);

        // 메세지 추가 시 유저와 채널에도 추가
        sentFrom.addMessage(message);
        this.userService.update(sentFrom.getId(), sentFrom);

        channel.addMessage(message);
        this.channelService.update(channel.getId(), channel);

        Map<UUID, Message> messages = this.readMessageFile();
        messages.put(message.getId(), message);
        this.writeMessageFile(messages);
        return message;
    }

    @Override
    public Message update(UUID id, Message newMessage) {
        Map<UUID, Message> messages = this.readMessageFile();
        Message message = Optional.ofNullable(messages.get(id)).orElseThrow(
                () -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다."));
        message.validateNotDeleted();
        message.update(newMessage);
        this.writeMessageFile(messages);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Map<UUID, Message> messages = this.readMessageFile();
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public List<Message> findAll() {
        Map<UUID, Message> messages = this.readMessageFile();
        return messages.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> messages = this.readMessageFile();
        Message message = Optional.ofNullable(messages.get(id)).orElseThrow(
                () -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다."));

        // 메세지 삭제 시 채널과 유저에도 메세지 삭제
        Channel channel = message.getChannel();
        channel.removeMessage(id);
        this.channelService.update(channel.getId(), channel);

        User user = message.getSentFrom();
        user.removeMessage(id);
        this.userService.update(user.getId(), user);

        message.softDelete();
        this.update(id, message);
    }

    public Map<UUID, Message> readMessageFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./data/message.ser"))) {
            @SuppressWarnings("unchecked")
            Map<UUID, Message> messages = (Map<UUID, Message>) ois.readObject();
            return messages;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError("직렬화된 객체의 클래스를 찾을 수 없습니다.");
        }
    }

    public void writeMessageFile(Map<UUID, Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./data/message.ser"))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        }
    }
}
