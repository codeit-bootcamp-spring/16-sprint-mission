package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private final File file;

    public FileMessageRepository() {
        this.file = new File("data/messages.dat");

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    @Override
    public Message save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메시지 정보는 필수입니다.");
        }

        Map<UUID, Message> data = readAllAsMap();
        data.put(message.getId(), message);
        writeAll(data);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(readAllAsMap().get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(readAllAsMap().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Message> data = readAllAsMap();

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        data.remove(id);
        writeAll(data);
    }

    private Map<UUID, Message> readAllAsMap() {
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("메시지 파일을 읽을 수 없습니다.", e);
        }
    }

    private void writeAll(Map<UUID, Message> data) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException("메시지 파일을 저장할 수 없습니다.", e);
        }
    }
}
