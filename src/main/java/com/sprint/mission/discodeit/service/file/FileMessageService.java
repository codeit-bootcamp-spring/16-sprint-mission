package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private final File file = new File("data/messages.ser");
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메세지 읽기 실패", e);
        }
    }

    private void save(Map<UUID, Message> data) {
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("메세지 저장 실패", e);
        }
    }

    @Override
    public Message create(UUID channelId, UUID senduser, String content) {
        if (userService.read(senduser) == null){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다" + senduser);
        }
        if (channelService.read(channelId) == null){
            throw new IllegalArgumentException("존재하지 않는 채널입니다" + channelId);
        }
        Map<UUID, Message> data = load();
        Message message = new Message(channelId, senduser, content);
        data.put(message.getId(), message);
        save(data);
        return message;
    }

    @Override
    public Message read(UUID id) {

        return load().get(id);
    }

    @Override
    public List<Message> readAll() {

        return new ArrayList<>(load().values());
    }

    @Override
    public Message update(UUID id, String content) {
        Map<UUID, Message> data = load();
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다." + id);
        }
        message.update(content);
        save(data);
        return message;
    }

    @Override
    public List<Message> findByContent(String keyword) {
        return load().values().stream()
                .filter(message -> message.getContent().contains(keyword))
                        .collect(Collectors.toList());
    }

    @Override
    public Message toggleBookmark(UUID id) {
        Map<UUID, Message> data = load();
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다." + id);
        }
        message.toggleBookmark();
        save(data);
        return message;
    }
    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = load();
        data.remove(id);
        save(data);
    }


}
