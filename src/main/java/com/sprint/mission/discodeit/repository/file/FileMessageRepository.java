package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "temp/messages.ser";

    private final Map<UUID, Message> data;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public FileMessageRepository(UserRepository userService, ChannelRepository channelService) throws IOException {
        this.data = new HashMap<>();
        this.userRepository = userService;
        this.channelRepository = channelService;

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object findObject = ois.readObject();
            Map<UUID, Message> loaded = (Map<UUID, Message>) findObject;
            data.putAll(loaded);
        } catch (FileNotFoundException e) {
            Path file = Path.of(FILE_PATH);
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message create(Message message) {
        if (userRepository.findById(message.getUser().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 User는 Message를 작성할 수 없습니다.");
        }
        if (channelRepository.findById(message.getChannel().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 Channel에는 Message를 작성할 수 없습니다.");
        }
        data.put(message.getId(), message);
        save();

        // 작성자(users.ser)와 Channel(channels.ser)이 보관하는 Message 목록에도 반영하고 저장한다.
        userRepository.addMessage(message);
        channelRepository.addMessage(message);
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

        userRepository.refreshMessage(message);
        channelRepository.refreshMessage(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
        save();

        // User/Channel이 보관 중인 Message 목록에서도 제거한다.
        userRepository.removeMessage(id);
        channelRepository.removeMessage(id);
    }

    @Override
    public void refreshUser(User user) {
        MessageRepository.super.refreshUser(user);
        save();
    }

    @Override
    public void refreshChannel(Channel channel) {
        MessageRepository.super.refreshChannel(channel);
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
