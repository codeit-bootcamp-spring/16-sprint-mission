package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** MessageRepository를 객체 직렬화를 이용한 개별 파일로 구현합니다. */
@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileMessageRepository implements MessageRepository {

    private final Path directory;

    @Autowired
    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory}") String baseDirectory
    ) {
        this(Paths.get(baseDirectory).resolve("messages"));
    }

    public FileMessageRepository(Path directory) {
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "메시지 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(Message message) {
        write(fileFor(message.getId()), message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path file = fileFor(id);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        return Optional.of(read(file));
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try (var files = Files.list(directory)) {
            files.filter(path ->
                            path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> messages.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "메시지 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        return List.copyOf(messages);
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(fileFor(id));
    }

    @Override
    public boolean deleteById(UUID id) {
        try {
            return Files.deleteIfExists(fileFor(id));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "메시지 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        // 이 규칙을 메서드 하나에 두어 저장·조회·삭제가 같은 경로를 사용합니다.
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, Message message) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(message);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "메시지 파일을 저장할 수 없습니다: " + file, e
            );
        }
    }

    private Message read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "메시지 파일을 읽을 수 없습니다: " + file, e
            );
        }
    }
}
