
package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private final File directory;

    public FileMessageRepository() {
        this.directory = new File("data/messages");

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("메시지 폴더를 생성할 수 없습니다.");
        }
    }

    private File getFile(UUID id) {
        return new File(directory, id + ".ser");
    }

    @Override
    public void save(Message message) {
        File file = getFile(message.getId());

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(message);
        } catch (IOException e) {
            throw new IllegalStateException("메시지를 저장할 수 없습니다.", e);
        }
    }

    private Message load(File file) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            return (Message) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("메시지를 불러올 수 없습니다.", e);
        }
    }

    @Override
    public Message getById(UUID id) {
        File file = getFile(id);

        if (!file.exists()) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        return load(file);
    }

    @Override
    public List<Message> getAll() {
        List<Message> messages = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files == null) {
            throw new IllegalStateException("메시지 폴더를 읽을 수 없습니다.");
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".ser")) {
                messages.add(load(file));
            }
        }

        return messages;
    }

    @Override
    public void delete(UUID id) {
        File file = getFile(id);

        if (!file.exists()) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        if (!file.delete()) {
            throw new IllegalStateException("메시지를 삭제할 수 없습니다.");
        }
    }
}
