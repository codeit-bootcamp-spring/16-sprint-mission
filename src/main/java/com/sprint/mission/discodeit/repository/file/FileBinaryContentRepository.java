package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path directory;

    @Autowired
    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory}") String baseDirectory
    ) {
        this(Paths.get(baseDirectory).resolve("binary-contents"));
    }

    public FileBinaryContentRepository(Path directory) {
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "바이너리 콘텐츠 저장 디렉터리를 만들 수 없습니다: " + directory, e
            );
        }
    }

    @Override
    public void save(BinaryContent binaryContent) {
        write(fileFor(binaryContent.getId()), binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path file = fileFor(id);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        return Optional.of(read(file));
    }

    @Override
    public List<BinaryContent> findAll() {
        List<BinaryContent> contents = new ArrayList<>();

        try (var files = Files.list(directory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .sorted()
                    .forEach(path -> contents.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "바이너리 콘텐츠 파일 목록을 읽을 수 없습니다: " + directory, e
            );
        }

        return List.copyOf(contents);
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
                    "바이너리 콘텐츠 파일을 삭제할 수 없습니다: " + id, e
            );
        }
    }

    private Path fileFor(UUID id) {
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, BinaryContent binaryContent) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(binaryContent);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "바이너리 콘텐츠를 저장할 수 없습니다: " + file, e
            );
        }
    }

    private BinaryContent read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (BinaryContent) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "바이너리 콘텐츠를 읽을 수 없습니다: " + file, e
            );
        }
    }
}
