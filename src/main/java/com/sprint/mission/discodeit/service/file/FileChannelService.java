package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelService implements ChannelService {

    private static final String FILE_PATH = "temp/channels.ser";

    private final Map<UUID, Channel> data;
    private UserService userService;
    private MessageService messageService;

    public FileChannelService() throws IOException {
        this.data = new HashMap<>();

        Path path = Path.of(FILE_PATH);
        Files.createDirectories(path.getParent());
        if (Files.notExists(path)) {
            return;
        }

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
            throw new IOException("사용자 데이터 형식이 올바르지 않습니다.", e);
        }
    }

    // 서비스끼리 서로 참조하므로(순환) 생성자 대신 setter로 연결한다.
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
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
        if (userService != null) {
            userService.refreshChannel(channel);
        }
        if (messageService != null) {
            messageService.refreshChannel(channel);
        }
        return channel;
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다.");
        }

        // Channel의 Message 삭제, 참여 중이던 User에서 Channel 제거
        if (messageService != null) {
            for (Message message : messageService.findAll()) {
                if (message.getChannel().getId().equals(id)) {
                    messageService.delete(message.getId());
                }
            }
        }
        if (userService != null) {
            userService.removeChannel(id);
        }

        data.remove(id);
        save();
    }

    @Override
    public void refreshUser(User user) {
        ChannelService.super.refreshUser(user);
        save();
    }

    @Override
    public void removeUser(UUID userId) {
        ChannelService.super.removeUser(userId);
        save();
    }

    @Override
    public void removeMessage(UUID messageId) {
        ChannelService.super.removeMessage(messageId);
        save();
    }

    @Override
    public void addMessage(Message message) {
        ChannelService.super.addMessage(message);
        save();
    }

    @Override
    public void refreshMessage(Message message) {
        ChannelService.super.refreshMessage(message);
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
