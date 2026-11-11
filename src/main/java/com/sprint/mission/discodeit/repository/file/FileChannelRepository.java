package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** 채널별 직렬화 파일을 저장소로 사용합니다. */
@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileChannelRepository implements ChannelRepository {

    private final Path directory;

    @Autowired
    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory}") String baseDirectory
    ) {
        this(Paths.get(baseDirectory).resolve("channels"));
    }

    public FileChannelRepository(Path directory) {
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "채널 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(Channel channel) {
        write(fileFor(channel.getId()), channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path file = fileFor(id);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        return Optional.of(read(file));
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();

        try (var files = Files.list(directory)) {
            files.filter(path ->
                            path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> channels.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "채널 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        // 디렉터리 나열 순서는 보장되지 않으므로 두 저장 방식의 결과 순서를 맞춥니다.
        channels.sort(
                Comparator.comparing(Channel::getCreatedAt)
                        .thenComparing(Channel::getId)
        );
        return List.copyOf(channels);
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
                    "채널 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, Channel channel) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(channel);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "채널 파일을 저장할 수 없습니다: " + file, e
            );
        }
    }

    private Channel read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (Channel) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "채널 파일을 읽을 수 없습니다: " + file, e
            );
        }
    }
}
