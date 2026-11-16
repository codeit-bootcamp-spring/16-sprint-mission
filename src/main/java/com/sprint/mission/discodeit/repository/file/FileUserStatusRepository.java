package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path directory;

    @Autowired
    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory}") String baseDirectory
    ) {
        this(Paths.get(baseDirectory).resolve("user-statuses"));
    }

    public FileUserStatusRepository(Path directory) {
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "사용자 상태 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(UserStatus userStatus) {
        write(fileFor(userStatus.getId()), userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path file = fileFor(id);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        return Optional.of(read(file));
    }

    @Override
    public List<UserStatus> findAll() {
        List<UserStatus> userStatuses = new ArrayList<>();

        try (var files = Files.list(directory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .sorted()
                    .forEach(path -> userStatuses.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "사용자 상태 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        return List.copyOf(userStatuses);
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(fileFor(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public boolean deleteById(UUID id) {
        try {
            return Files.deleteIfExists(fileFor(id));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "사용자 상태 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, UserStatus userStatus) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(userStatus);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "사용자 상태를 저장할 수 없습니다: " + file, e
            );
        }
    }

    private UserStatus read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (UserStatus) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "사용자 상태를 읽을 수 없습니다: " + file, e
            );
        }
    }
}
