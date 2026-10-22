package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.entity.BaseEntity;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileObjectStore<T extends BaseEntity> {
    private final Path directory;
    private final Class<T> type;

    public FileObjectStore(Path directory, Class<T> type) {
        this.directory = directory;
        this.type = type;
    }

    public T save(T entity) {
        try {
            Files.createDirectories(directory);
            Path file = directory.resolve(entity.getId() + ".ser");

            try (ObjectOutputStream output =
                         new ObjectOutputStream(Files.newOutputStream(file))) {
                output.writeObject(entity);
            }

            return entity;
        } catch (IOException e) {
            throw new IllegalStateException("객체 파일 저장 실패", e);
        }
    }

    public T findById(UUID id) {
        if (id == null) {
            return null;
        }

        Path file = directory.resolve(id + ".ser");

        if (!Files.exists(file)) {
            return null;
        }

        return read(file);
    }

    public List<T> findAll() {
        List<T> result = new ArrayList<>();

        if (!Files.exists(directory)) {
            return result;
        }

        try (Stream<Path> paths = Files.list(directory)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .sorted()
                    .forEach(path -> result.add(read(path)));

            return result;
        } catch (IOException e) {
            throw new IllegalStateException("객체 파일 목록 조회 실패", e);
        }
    }

    public void delete(UUID id) {
        if (id == null) {
            return;
        }

        try {
            Files.deleteIfExists(directory.resolve(id + ".ser"));
        } catch (IOException e) {
            throw new IllegalStateException("객체 파일 삭제 실패", e);
        }
    }

    private T read(Path file) {
        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return type.cast(input.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("객체 파일 읽기 실패: " + file, e);
        }
    }
}