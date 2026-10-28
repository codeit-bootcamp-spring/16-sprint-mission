package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMessageService implements MessageService {

    private static final String FILE_PATH = "temp/messages.ser";

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) throws IOException {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;

        Path path = Path.of(FILE_PATH);
        Files.createDirectories(path.getParent());
        if (Files.notExists(path)) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object findObject = ois.readObject();
            Map<UUID, Message> loaded = (Map<UUID, Message>) findObject;
            data.putAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            throw new IOException("사용자 데이터 형식이 올바르지 않습니다.", e);
        }
    }

    @Override
    public Message create(Message message) {
        if (userService.findById(message.getUser().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 User는 Message를 작성할 수 없습니다.");
        }
        if (channelService.findById(message.getChannel().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 Channel에는 Message를 작성할 수 없습니다.");
        }
        data.put(message.getId(), message);
        save();

        // 작성자(users.ser)와 Channel(channels.ser)이 보관하는 Message 목록에도 반영하고 저장한다.
        userService.addMessage(message);
        channelService.addMessage(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, String newMessage) {
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
        message.update(newMessage);
        save();

        userService.refreshMessage(message);
        channelService.refreshMessage(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
        save();

        // User/Channel이 보관 중인 Message 목록에서도 제거한다.
        userService.removeMessage(id);
        channelService.removeMessage(id);
    }

    @Override
    public void refreshUser(User user) {
        MessageService.super.refreshUser(user);
        save();
    }

    @Override
    public void refreshChannel(Channel channel) {
        MessageService.super.refreshChannel(channel);
        save();
    }

    private void save() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
