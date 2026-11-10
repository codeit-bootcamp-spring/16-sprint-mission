
package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path directory = Path.of("data", "user-status");

    public FileUserStatusRepository() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("저장 폴더 생성 실패", e);
        }
    }

    @Override
    public void save(UserStatus userStatus) {
        Path path = getPath(userStatus.getId());

        try (ObjectOutputStream output =
                     new ObjectOutputStream(Files.newOutputStream(path))) {
            output.writeObject(userStatus);
        } catch (IOException e) {
            throw new IllegalStateException("접속 상태 저장 실패", e);
        }
    }

    @Override
    public UserStatus getById(UUID id) {
        Path path = getPath(id);

        if (!Files.exists(path)) {
            return null;
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(path))) {
            return (UserStatus) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("접속 상태 조회 실패", e);
        }
    }

    @Override
    public List<UserStatus> getAll() {
        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> getById(
                            UUID.fromString(
                                    path.getFileName().toString().replace(".ser", "")
                            )))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("접속 상태 목록 조회 실패", e);
        }
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return getAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void delete(UUID id) {
        try {
            Files.deleteIfExists(getPath(id));
        } catch (IOException e) {
            throw new IllegalStateException("접속 상태 삭제 실패", e);
        }
    }

    private Path getPath(UUID id) {
        return directory.resolve(id + ".ser");
    }
}
