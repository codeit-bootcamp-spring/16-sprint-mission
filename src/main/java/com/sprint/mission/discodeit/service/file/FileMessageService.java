package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
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
        if (userService.findById(message.getUser().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 User는 Message를 작성할 수 없습니다.");
        }
        if (channelService.findById(message.getChannel().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 Channel에는 Message를 작성할 수 없습니다.");
        }
        data.put(message.getId(), message);
        writeToFile();
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
        writeToFile();
        return message;
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
        writeToFile();
    }

    private void writeToFile() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
