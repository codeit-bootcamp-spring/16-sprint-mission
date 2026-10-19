package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "temp/messages.ser";

    private final Map<UUID, Message> data;

    public FileMessageRepository() throws IOException {
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
            Map<UUID, Message> loaded = (Map<UUID, Message>) findObject;
            data.putAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            throw new IOException("사용자 데이터 형식이 올바르지 않습니다.", e);
        }
    }

    @Override
    public Message create(Message message) {
        data.put(message.getId(), message);
        save();
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
    public Message update(Message message) {
        if (!data.containsKey(message.getId())) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
        data.put(message.getId(), message);
        save();
        return message;
    }

    @Override
    public void updateAll(List<Message> messages) {
        if (!messages.stream().allMatch(message -> data.containsKey(message.getId()))) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }

        messages.forEach(message -> data.put(message.getId(), message));
        save();
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
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
