package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 기본 요구사항의 File 서비스 예제입니다.
 * 메시지 업무 규칙과 File IO 저장을 직접 수행합니다.
 * 심화 과제의 BasicMessageService는 같은 저장 작업을 MessageRepository에 위임합니다.
 */
public class FileMessageService {

    private static final String EXTENSION = ".ser";

    private final Path directory;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this(
                userService,
                channelService,
                Paths.get(System.getProperty("user.dir"), "data", "messages")
        );
    }

    public FileMessageService(
            UserService userService,
            ChannelService channelService,
            Path directory
    ) {
        if (userService == null || channelService == null) {
            throw new IllegalArgumentException("Related services cannot be null.");
        }
        this.userService = userService;
        this.channelService = channelService;
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("메시지 저장 디렉터리를 만들 수 없습니다: " + directory, e);
        }
    }

    public void create(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }

        Path file = fileFor(message.getId());
        if (Files.exists(file)) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + message.getId());
        }

        userService.findById(message.getAuthorId());
        channelService.findById(message.getChannelId());

        write(file, message);
    }

    public Message findById(UUID id) {
        Path file = fileFor(id);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Not Found Message id: " + id);
        }
        return read(file);
    }

    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (var files = Files.list(directory)) {
            files.filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .forEach(path -> messages.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException("메시지 파일 목록을 읽을 수 없습니다: " + directory, e);
        }
        return List.copyOf(messages);
    }

    public void update(UUID id, String content) {
        Message existingMessage = findById(id);
        existingMessage.update(content);
        write(fileFor(id), existingMessage);
    }

    public void delete(UUID id) {
        try {
            if (!Files.deleteIfExists(fileFor(id))) {
                throw new IllegalArgumentException("Message not found: " + id);
            }
        } catch (IOException e) {
            throw new IllegalStateException("메시지 파일을 삭제할 수 없습니다: " + id, e);
        }
    }

    private Path fileFor(UUID id) {
        return directory.resolve(id + EXTENSION);
    }

    private void write(Path file, Message message) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(message);
        } catch (IOException e) {
            throw new IllegalStateException("메시지 파일을 저장할 수 없습니다: " + file, e);
        }
    }

    private Message read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("메시지 파일을 읽을 수 없습니다: " + file, e);
        }
    }
}
