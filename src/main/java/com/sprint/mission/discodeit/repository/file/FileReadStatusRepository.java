package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private static final Path DIRECTORY =
            Paths.get(
                    System.getProperty("user.dir"),
                    "data",
                    "readStatuses"
            );

    public FileReadStatusRepository() {
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ReadStatus load(Path filePath) {
        try (
                FileInputStream fis =
                        new FileInputStream(filePath.toFile());
                ObjectInputStream ois =
                        new ObjectInputStream(fis)
        ) {
            return (ReadStatus) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path filePath =
                DIRECTORY.resolve(readStatus.getId() + ".ser");

        try (
                FileOutputStream fos =
                        new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos =
                        new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return readStatus;
    }

    @Override
    public List<ReadStatus> findAll() {
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
    public Optional<ReadStatus> findById(UUID id) {
        Path filePath =
                DIRECTORY.resolve(id + ".ser");

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        return Optional.of(load(filePath));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void delete(ReadStatus readStatus) {
        Path filePath =
                DIRECTORY.resolve(readStatus.getId() + ".ser");

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
