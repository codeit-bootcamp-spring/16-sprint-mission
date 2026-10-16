package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "temp/channels.ser";

    private final Map<UUID, Channel> data;
    private UserRepository userRepository;
    private MessageRepository messageRepository;

    public FileChannelRepository() throws IOException {
        this.data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object findObject = ois.readObject();
            Map<UUID, Channel> loaded = (Map<UUID, Channel>) findObject;
            data.putAll(loaded);
        } catch (FileNotFoundException e) {
            Path file = Path.of(FILE_PATH);
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 서비스끼리 서로 참조하므로(순환) 생성자 대신 setter로 연결한다.
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        save();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, ChannelType channelType, String title, String description) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다.");
        }
        channel.update(channelType, title, description);
        save();

        // User/Message 파일에 저장된 Channel 복사본도 최신 정보로 맞춘다.
        if (userRepository != null) {
            userRepository.refreshChannel(channel);
        }
        if (messageRepository != null) {
            messageRepository.refreshChannel(channel);
        }
        return channel;
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다.");
        }

        // Channel의 Message 삭제, 참여 중이던 User에서 Channel 제거
        if (messageRepository != null) {
            for (Message message : messageRepository.findAll()) {
                if (message.getChannel().getId().equals(id)) {
                    messageRepository.delete(message.getId());
                }
            }
        }
        if (userRepository != null) {
            userRepository.removeChannel(id);
        }

        data.remove(id);
        save();
    }

    @Override
    public void refreshUser(User user) {
        ChannelRepository.super.refreshUser(user);
        save();
    }

    @Override
    public boolean addUser(UUID channelId, User user) {
        boolean added = ChannelRepository.super.addUser(channelId, user);
        if (added) {
            save();
        }
        return added;
    }

    @Override
    public boolean removeUser(UUID channelId, UUID userId) {
        boolean removed = ChannelRepository.super.removeUser(channelId, userId);
        if (removed) {
            save();
        }
        return removed;
    }

    @Override
    public void removeUser(UUID userId) {
        ChannelRepository.super.removeUser(userId);
        save();
    }

    @Override
    public void removeMessage(UUID messageId) {
        ChannelRepository.super.removeMessage(messageId);
        save();
    }

    @Override
    public void addMessage(Message message) {
        ChannelRepository.super.addMessage(message);
        save();
    }

    @Override
    public void refreshMessage(Message message) {
        ChannelRepository.super.refreshMessage(message);
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
