package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final Path PATH = Paths.get("data", "binaryContents.ser");

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Map<UUID, BinaryContent> data = load();          // 1. 읽기
        data.put(binaryContent.getId(), binaryContent);           // 2. 넣기 (같은 id면 덮어쓰기)
        writeToFile(data);                      // 3. 쓰기
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(load().get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        Map<UUID, BinaryContent> data = load();
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, BinaryContent> data = load();
        data.remove(id);
        writeToFile(data);
    }

    private void writeToFile(Map<UUID, BinaryContent> data) {
        try {
            Files.createDirectories(PATH.getParent());
            try (ObjectOutputStream oos =
                         new ObjectOutputStream(Files.newOutputStream(PATH))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> load() {
        if (!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
