package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final Path filePath = Path.of("data","messages.ser");

    public FileMessageRepository() {
        try{
            Files.createDirectories(filePath.getParent());
        }catch (IOException e){
            throw new IllegalStateException("저장폴더 생성 못함",e);
        }
    }
    private void saveMessages(List<Message> messages) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
            out.writeObject(messages);
        } catch (IOException e) {
            throw new IllegalStateException("메시지 목록을 저장할 수 없습니다", e);
        }
    }
    private List<Message> loadMessages() {
        if(!Files.exists(filePath)){
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            return (List<Message>) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("메시지 목록을 읽을 수 없습니다",e);
        }
    }

    @Override
    public Message save(Message message) {
        List<Message> messages = loadMessages();
        messages.removeIf(m -> m.getId().equals(message.getId()));
        messages.add(message);
        saveMessages(messages);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return loadMessages().stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    @Override
    public List<Message> findAll() {
        return loadMessages();
    }

    @Override
    public void deleteById(UUID id) {
        List<Message> messages = loadMessages();
        messages.removeIf(m -> m.getId().equals(id));
        saveMessages(messages);
    }
}