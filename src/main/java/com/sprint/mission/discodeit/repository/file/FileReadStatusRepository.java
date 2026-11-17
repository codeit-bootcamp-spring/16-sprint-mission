package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path directory;
    private final FileLockProvider fileLockProvider;

    public FileReadStatusRepository() {
        this(
                Path.of(System.getProperty("user.dir"), "data", "readStatuses"),
                new FileLockProvider()
        );
    }

    public FileReadStatusRepository(Path directory) {
        this(directory, new FileLockProvider());
    }

    public FileReadStatusRepository(
            Path directory,
            FileLockProvider fileLockProvider
    ) {
        this.directory = directory;
        this.fileLockProvider = fileLockProvider;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "읽음 상태 저장 폴더 생성에 실패했습니다.", e);
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
            throw new RuntimeException(
                    "읽음 상태 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        Path path = fileFor(readStatusId);
        ReentrantLock lock = fileLockProvider.getLock(path);

        lock.lock();
        try {
            if (!Files.exists(path)) {
                return Optional.empty();
            }

            return Optional.of(readReadStatus(path));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<ReadStatus> findAll() {
        try (var paths = Files.list(directory)) {
            return paths
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        ReentrantLock lock =
                                fileLockProvider.getLock(path);

                        lock.lock();
                        try {
                            if (!Files.exists(path)) {
                                return null;
                            }

                            return readReadStatus(path);
                        } finally {
                            lock.unlock();
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(
                    "읽음 상태 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = fileFor(readStatus.getId());
        ReentrantLock lock = fileLockProvider.getLock(path);

        lock.lock();
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    Files.newOutputStream(path))) {
                oos.writeObject(readStatus);
                return readStatus;
            } catch (IOException e) {
                throw new RuntimeException(
                        "읽음 상태 저장에 실패했습니다.", e);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(UUID readStatusId) {
        Path path = fileFor(readStatusId);
        ReentrantLock lock = fileLockProvider.getLock(path);

        lock.lock();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(
                    "읽음 상태 삭제에 실패했습니다.", e);
        } finally {
            lock.unlock();
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
