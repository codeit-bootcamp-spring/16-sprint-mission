package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final Path DIRECTORY =
            Paths.get(
                    System.getProperty("user.dir"),
                    "data",
                    "binaryContents"
            );

    public FileBinaryContentRepository() {
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BinaryContent load(Path filePath) {
        try (
                FileInputStream fis =
                        new FileInputStream(filePath.toFile());
                ObjectInputStream ois =
                        new ObjectInputStream(fis)
        ) {
            return (BinaryContent) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path filePath =
                DIRECTORY.resolve(binaryContent.getId() + ".ser");

        try (
                FileOutputStream fos =
                        new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos =
                        new ObjectOutputStream(fos)
        ) {
            oos.writeObject(binaryContent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return binaryContent;
    }

    @Override
    public List<BinaryContent> findAll() {
        try (var paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.toString().endsWith(".ser"))
                    .map(this::load)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path filePath =
                DIRECTORY.resolve(id + ".ser");

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        return Optional.of(load(filePath));
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        Path filePath =
                DIRECTORY.resolve(binaryContent.getId() + ".ser");

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}