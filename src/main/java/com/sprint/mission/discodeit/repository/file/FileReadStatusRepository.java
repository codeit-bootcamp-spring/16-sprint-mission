package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path directory;

    public FileReadStatusRepository() {
        this(Path.of(System.getProperty("user.dir"), "data", "readStatuses"));
    }

    public FileReadStatusRepository(Path directory) {
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("읽음 상태 저장 폴더 생성에 실패했습니다.", e);
        }
    }

    private Path fileFor(UUID readStatusId) {
        return directory.resolve(readStatusId + ".ser");
    }

    private ReadStatus readReadStatus(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(path))) {
            return (ReadStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("읽음 상태 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        Path path = fileFor(readStatusId);

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        return Optional.of(readReadStatus(path));
    }

    @Override
    public List<ReadStatus> findAll() {
        try (var paths = Files.list(directory)) {
            return paths
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::readReadStatus)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("읽음 상태 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = fileFor(readStatus.getId());

        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(path))) {
            oos.writeObject(readStatus);
            return readStatus;
        } catch (IOException e) {
            throw new RuntimeException("읽음 상태 저장에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteById(UUID readStatusId) {
        Path path = fileFor(readStatusId);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("읽음 상태 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(
            UUID userId,
            UUID channelId
    ) {
        return findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }
}