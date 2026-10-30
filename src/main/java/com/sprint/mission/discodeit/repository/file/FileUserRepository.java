package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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

/** UserRepository를 객체 직렬화를 이용한 파일 저장 방식으로 구현합니다. */
@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileUserRepository implements UserRepository {

    private final Path directory;

    @Autowired
    public FileUserRepository(
            @Value("${discodeit.repository.file-directory}") String baseDirectory
    ) {
        this(Paths.get(baseDirectory).resolve("users"));
    }

    public FileUserRepository(Path directory) {
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "사용자 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(User user) {
        write(fileFor(user.getId()), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path file = fileFor(id);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        return Optional.of(read(file));
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (var files = Files.list(directory)) {
            files.filter(path ->
                            path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> users.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "사용자 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        return List.copyOf(users);
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
                    "사용자 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, User user) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(user);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "사용자 파일을 저장할 수 없습니다: " + file, e
            );
        }
    }

    private User read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "사용자 파일을 읽을 수 없습니다: " + file, e
            );
        }
    }
}
