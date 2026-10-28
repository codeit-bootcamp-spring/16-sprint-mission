package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
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
public class FileMessageService implements MessageService {

    private static final String EXTENSION = ".ser";

    private final Path directory;
    private final UserService userService;
    private final ChannelService channelService;

    /** 기본 저장 폴더는 실행 디렉터리 아래 data/messages입니다. */
    public FileMessageService(UserService userService, ChannelService channelService) {
        this(
                userService,
                channelService,
                Paths.get(System.getProperty("user.dir"), "data", "messages")
        );
    }

    /** 저장 위치를 전달받아 실행 환경이나 실습별로 디렉터리를 분리합니다. */
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

    @Override
    public void create(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }

        Path file = fileFor(message.getId());
        if (Files.exists(file)) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + message.getId());
        }

        // 메시지가 참조하는 작성자와 채널이 있는지 서비스에 확인합니다.
        userService.findById(message.getUserId());
        channelService.findById(message.getChannelId());

        // 검증이 통과한 뒤 File IO로 메시지 객체를 직렬화합니다.
        write(file, message);
    }

    @Override
    public Message findById(UUID id) {
        Path file = fileFor(id);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Not Found Message id: " + id);
        }
        return read(file);
    }

    @Override
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

    @Override
    public void update(UUID id, String content) {
        Message existingMessage = findById(id);
        existingMessage.update(content);
        // 같은 ID 파일을 다시 열어 변경된 객체를 덮어씁니다.
        write(fileFor(id), existingMessage);
    }

    @Override
    public void delete(UUID id) {
        try {
            if (!Files.deleteIfExists(fileFor(id))) {
                throw new IllegalArgumentException("Message not found: " + id);
            }
        } catch (IOException e) {
            throw new IllegalStateException("메시지 파일을 삭제할 수 없습니다: " + id, e);
        }
    }

    /** UUID와 확장자를 결합해 메시지 저장 파일 경로를 만듭니다. */
    private Path fileFor(UUID id) {
        return directory.resolve(id + EXTENSION);
    }

    /** ObjectOutputStream으로 객체를 바이트 데이터로 직렬화해 파일에 기록합니다. */
    private void write(Path file, Message message) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(message);
        } catch (IOException e) {
            throw new IllegalStateException("메시지 파일을 저장할 수 없습니다: " + file, e);
        }
    }

    /** ObjectInputStream으로 파일 바이트를 Message 객체로 역직렬화합니다. */
    private Message read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("메시지 파일을 읽을 수 없습니다: " + file, e);
        }
    }
}
