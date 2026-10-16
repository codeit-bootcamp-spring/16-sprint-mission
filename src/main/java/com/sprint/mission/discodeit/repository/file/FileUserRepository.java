package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "temp/users.ser";

    private final Map<UUID, User> data;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;

    public FileUserRepository() throws IOException {
        this.data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object findObject = ois.readObject();
            Map<UUID, User> loaded = (Map<UUID, User>) findObject;
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
    public void setChannelRepository(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public User create(User user) {
        data.put(user.getId(), user);
        save();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String userName, String email, String password) {
        User user = data.get(id);
        if (user == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다.");
        }
        user.update(userName, email, password);
        save();

        // Channel/Message 파일에 저장된 User 복사본도 최신 정보로 맞춘다.
        if (channelRepository != null) {
            channelRepository.refreshUser(user);
        }
        if (messageRepository != null) {
            messageRepository.refreshUser(user);
        }
        return user;
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("존재하지 않는 User입니다.");
        }

        // User가 작성한 Message 삭제, 속해있던 Channel에서 User 제거
        if (messageRepository != null) {
            for (Message message : messageRepository.findAll()) {
                if (message.getUser().getId().equals(id)) {
                    messageRepository.delete(message.getId());
                }
            }
        }
        if (channelRepository != null) {
            channelRepository.removeUser(id);
        }

        data.remove(id);
        save();
    }

    @Override
    public void refreshChannel(Channel channel) {
        UserRepository.super.refreshChannel(channel);
        save();
    }

    @Override
    public void addChannel(UUID userId, Channel channel) {
        UserRepository.super.addChannel(userId, channel);
        save();
    }

    @Override
    public void removeChannel(UUID userId, UUID channelId) {
        UserRepository.super.removeChannel(userId, channelId);
        save();
    }

    @Override
    public void removeChannel(UUID channelId) {
        UserRepository.super.removeChannel(channelId);
        save();
    }

    @Override
    public void removeMessage(UUID messageId) {
        UserRepository.super.removeMessage(messageId);
        save();
    }

    @Override
    public void addMessage(Message message) {
        UserRepository.super.addMessage(message);
        save();
    }

    @Override
    public void refreshMessage(Message message) {
        UserRepository.super.refreshMessage(message);
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
