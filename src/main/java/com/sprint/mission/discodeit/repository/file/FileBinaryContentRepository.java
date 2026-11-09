package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileBinaryContentRepository
        implements BinaryContentRepository {

    private final Path directory;

    public FileBinaryContentRepository(Path root) {
        this.directory = root.resolve("binary-contents");
    }

    @Override
    public BinaryContent save(BinaryContent entity) {
        try {
            Files.createDirectories(directory);

            Path path = directory.resolve(
                    entity.getId() + ".ser"
            );

            try (ObjectOutputStream out = new ObjectOutputStream(
                    Files.newOutputStream(path))) {
                out.writeObject(entity);
            }

            return entity;
        } catch (IOException e) {
            throw new IllegalStateException("바이너리 저장 실패", e);
        }
    }

    @Override
    public BinaryContent findById(UUID id) {
        if (id == null) {
            return null;
        }

        Path path = directory.resolve(id + ".ser");

        if (!Files.exists(path)) {
            return null;
        }

        return read(path);
    }

    @Override
    public List<BinaryContent> findAll() {
        if (!Files.exists(directory)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.list(directory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName().toString().endsWith(".ser"))
                    .sorted()
                    .map(this::read)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "바이너리 목록 조회 실패", e
            );
        }
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            return;
        }

        try {
            Files.deleteIfExists(
                    directory.resolve(id + ".ser")
            );
        } catch (IOException e) {
            throw new IllegalStateException("바이너리 삭제 실패", e);
        }
    }

    private BinaryContent read(Path path) {
        try (ObjectInputStream in = new ObjectInputStream(
                Files.newInputStream(path))) {
            return (BinaryContent) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "바이너리 읽기 실패: " + path, e
            );
        }
    }
}