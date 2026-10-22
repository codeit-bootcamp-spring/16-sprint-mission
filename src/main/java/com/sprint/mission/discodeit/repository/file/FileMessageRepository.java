package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageRepository implements MessageRepository {

    private final Path messageDirectory;

    public FileMessageRepository() {
        this(Path.of("data", "messages"));
    }

    public FileMessageRepository(Path messageDirectory) {
        this.messageDirectory = messageDirectory;

        try {
            Files.createDirectories(messageDirectory);
        } catch (IOException e) {
            throw new RuntimeException("메시지 디렉터리 생성에 실패했습니다.", e);
        }
    }

    private Path fileFor(UUID messageId) {
        return messageDirectory.resolve(messageId + ".ser");
    }

    private Message readMessage(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public Message findById(UUID messageId) {
        Path file = fileFor(messageId);

        if (Files.notExists(file)) {
            return null;
        }

        return readMessage(file);
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try (Stream<Path> files = Files.list(messageDirectory)) {
            files.filter(file -> file.getFileName().toString().endsWith(".ser"))
                    .forEach(file -> messages.add(readMessage(file)));
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 목록을 읽는 데 실패했습니다.", e);
        }

        return messages;
    }

    @Override
    public Message save(Message message) {
        Path file = fileFor(message.getId());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(message);
            return message;
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteById(UUID messageId) {
        try {
            Files.deleteIfExists(fileFor(messageId));
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public List<Message> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .filter(message -> message.getSenderId().equals(userId)
                        && message.getChannelId().equals(channelId))
                .toList();
    }
}
