package com.sprint.mission.discodeit.repository.file;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileMessageRepository implements MessageRepository {
    final Path PATH = Paths.get("messages.dat");
    Map<UUID, Message> data = new ConcurrentHashMap<>();

    public FileMessageRepository() {
        loadFile();
    }

    @Override
    public void save(Message message) {
        if (message == null || message.getId() == null) {
            throw new NullPointerException("메시지 데이터가 올바르지 않습니다.");
        }
        if (message.getUserId() == null || message.getChannelId() == null) {
            throw new NullPointerException("메시지의 유저 ID 또는 채널 ID 값이 존재하지 않습니다.");
        }
        data.put(message.getId(), message);
        saveFile();
    }

    @Override
    public Message findId(UUID id) {
        if (id == null) {
            throw new NullPointerException("조회하려는 메시지 ID가 null입니다.");
        }

        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteId(UUID id) {
        if (id == null) {
            throw new NullPointerException("삭제하려는 메시지 ID가 null입니다.");
        }

        data.remove(id);
        saveFile();
    }

    private void saveFile() {
        try (OutputStream os = Files.newOutputStream(PATH);
             ObjectOutputStream oos = new ObjectOutputStream(os))
        {
            oos.reset();
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 저장 중 치명적인 오류가 발생했습니다. 파일: " + PATH, e);
        }
    }

    private void loadFile() {
        if (!Files.exists(PATH)) return;

        try (InputStream is = Files.newInputStream(PATH);
             ObjectInputStream ois = new ObjectInputStream(is))
        {
            this.data = (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 파일 로딩 중 치명적인 오류가 발생했습니다. 파일: " + PATH, e);
        }
    }
}
