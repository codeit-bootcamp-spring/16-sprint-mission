package com.sprint.mission.discodeit.service.file.file_service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

public class FileMessageService implements MessageService {

    private static final String FILE_PATH = "temp/messages.ser";

    private final Map<UUID, Message> data;

    public FileMessageService() throws IOException {
        this.data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object findObject = ois.readObject();
            Map<UUID, Message> messageMap = (Map<UUID, Message>) findObject;
            data.putAll(messageMap);
        } catch (FileNotFoundException e) {
            Path messageFile = Path.of(FILE_PATH);
            Files.createFile(messageFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message create(String message, Channel channel, User user) {
        Message newMessage = new Message(message, channel, user);
        data.put(newMessage.getId(), newMessage);
        user.addMessage(newMessage);
        channel.addMessage(newMessage);
        save();

        return newMessage;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            System.out.println("존재하지 않는 메시지입니다.");
            return Optional.empty();
        }
        return Optional.of(message);
    }

    @Override
    public List<Message> findAll() {
        Collection<Message> values = data.values();
        return new ArrayList<>(values);
    }

    @Override
    public Optional<Message> update(UUID id, String newMessage) {
        Optional<Message> message = findById(id);
        if (message.isEmpty()) {
            System.out.println("존재하지 않는 메시지의 내용을 수정할 수 없습니다.");
            return message;
        }
        message.get().setMessage(newMessage);
        message.get().setUpdatedAt(Instant.now().getEpochSecond());
        save();
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = data.remove(id);
        if (message == null) {
            throw new NoSuchElementException();
        }

        message.getUser().deleteMessage(message);
        message.getChannel().deleteMessage(message);
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
