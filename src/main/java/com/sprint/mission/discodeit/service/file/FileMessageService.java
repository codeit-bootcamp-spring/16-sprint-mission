package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMessageService implements MessageService {
    private static final Path PATH = Paths.get("data", "messages.ser");
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) { // DI 의존성 주입
        this.userService = userService;
        this.channelService = channelService;
    }
    @Override
    public Message create(Message message) {
        Map<UUID, Message> data = load();
        userService.findById(message.getSenderId());
        Channel channel = channelService.findById(message.getChannelId());
        if (!channel.hasUser(message.getSenderId())) {                      // 채널 멤버인지 검증 /
            throw new IllegalArgumentException("채널 멤버만 메시지를 작성할 수 있습니다.");
        }
        data.put(message.getId(), message);
        userService.addMessage(message.getSenderId(), message.getId());
        channelService.addMessage(message.getChannelId(), message.getId());
        save(data);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return getOrThrow(load(), id);
    }

    @Override
    public List<Message> findAll() {
        return load().values().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return load().values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public Message update(UUID id, UUID requesterId, String content) {
        Map<UUID, Message> data = load();
        Message message = getOrThrow(data, id);
        requireSender(message, requesterId, "메시지 수정 권한이 없습니다.");
        message.update(content);
        save(data);

        return message;
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Map<UUID, Message> data = load();
        Message message = getOrThrow(data, id);

        requireSender(message, requesterId, "메시지 삭제 권한이 없습니다.");
        data.remove(id);
        userService.removeMessage(message.getSenderId(), id);
        channelService.removeMessage(message.getChannelId(), id);
        save(data);
    }

    private static Message getOrThrow(Map<UUID, Message> data , UUID id) {
        Message message = data.get(id);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }
        return message;
    }

    private void requireSender(Message message, UUID requesterId, String errorMessage) {
        if(!message.getSenderId().equals(requesterId)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void save(Map<UUID, Message> data) {
        try {
            Files.createDirectories(PATH.getParent());           // data/ 폴더 보장
            try (ObjectOutputStream oos =
                         new ObjectOutputStream(Files.newOutputStream(PATH))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> load() {
        if (!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
