
package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path directory = Path.of("data", "read-status");

    public FileReadStatusRepository() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("저장 폴더 생성 실패", e);
        }
    }

    @Override
    public void save(ReadStatus readStatus) {
        Path path = getPath(readStatus.getId());

        try (ObjectOutputStream output =
                     new ObjectOutputStream(Files.newOutputStream(path))) {
            output.writeObject(readStatus);
        } catch (IOException e) {
            throw new IllegalStateException("읽음 상태 저장 실패", e);
        }
    }

    @Override
    public ReadStatus getById(UUID id) {
        Path path = getPath(id);

        if (!Files.exists(path)) {
            return null;
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(path))) {
            return (ReadStatus) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("읽음 상태 조회 실패", e);
        }
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return getAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return getAll().stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return getAll().stream()
                .filter(status -> status.getUserId().equals(userId)
                        && status.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public void delete(UUID id) {
        try {
            Files.deleteIfExists(getPath(id));
        } catch (IOException e) {
            throw new IllegalStateException("읽음 상태 삭제 실패", e);
        }
    }

    private Path getPath(UUID id) {
        return directory.resolve(id + ".ser");
    }

    private List<ReadStatus> getAll() {
        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> getById(
                            UUID.fromString(
                                    path.getFileName().toString().replace(".ser", "")
                            )))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("읽음 상태 목록 조회 실패", e);
        }
    }
}
