package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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

/**
 * ReadStatus를 파일로 저장하고 조회하는 Repository 구현체입니다.
 * @Repository가 있어서 Spring이 Bean으로 찾아 BasicChannelService에 주입할 수 있습니다.
 */
@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path directory;

    @Autowired
    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory}") String baseDirectory
    ) {
        this(Paths.get(baseDirectory).resolve("read-statuses"));
    }

    public FileReadStatusRepository(Path directory) {
        this.directory = directory;

        try {
            // 저장 폴더가 아직 없으면 미리 만듭니다.
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "읽음 상태 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(ReadStatus readStatus) {
        // 객체의 ID를 파일명으로 사용해 저장합니다.
        write(fileFor(readStatus.getId()), readStatus);
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Path file = fileFor(id);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        return Optional.of(read(file));
    }

    @Override
    public List<ReadStatus> findAll() {
        List<ReadStatus> readStatuses = new ArrayList<>();

        try (var files = Files.list(directory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .sorted()
                    .forEach(path -> readStatuses.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "읽음 상태 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        return List.copyOf(readStatuses);
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(fileFor(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(
            UUID userId,
            UUID channelId
    ) {
        // 사용자가 해당 채널에 참여했는지 찾을 때 이 두 ID를 함께 비교합니다.
        return findAll().stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId)
                                && readStatus.getChannelId().equals(channelId)
                )
                .findFirst();
    }

    @Override
    public boolean deleteById(UUID id) {
        try {
            // 파일이 실제로 삭제됐는지 결과를 돌려줍니다.
            return Files.deleteIfExists(fileFor(id));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "읽음 상태 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, ReadStatus readStatus) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(readStatus);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "읽음 상태를 저장할 수 없습니다: " + file, e
            );
        }
    }

    private ReadStatus read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (ReadStatus) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "읽음 상태를 읽을 수 없습니다: " + file, e
            );
        }
    }
}
