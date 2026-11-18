package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class FileUserStatusRepository implements UserStatusRepository {

    private final Path directory;
    private final FileLockProvider fileLockProvider;

    public FileUserStatusRepository() {
        this(
                Path.of(System.getProperty("user.dir"), "data", "userStatuses"),
                new FileLockProvider()
        );
    }

    public FileUserStatusRepository(Path directory) {
        this(directory, new FileLockProvider());
    }

    public FileUserStatusRepository(
            Path directory,
            FileLockProvider fileLockProvider
    ) {
        this.directory = directory;
        this.fileLockProvider = fileLockProvider;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "사용자 상태 저장 폴더 생성에 실패했습니다.", e);
        }
    }

    private Path fileFor(UUID userStatusId) {
        return directory.resolve(userStatusId + ".ser");
    }

    private UserStatus readUserStatus(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(path))) {
            return (UserStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(
                    "사용자 상태 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        Path path = fileFor(userStatusId);
        ReentrantLock lock = fileLockProvider.getLock(path);

        lock.lock();
        try {
            if (!Files.exists(path)) {
                return Optional.empty();
            }

            return Optional.of(readUserStatus(path));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<UserStatus> findAll() {
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

                            return readUserStatus(path);
                        } finally {
                            lock.unlock();
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(
                    "사용자 상태 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = fileFor(userStatus.getId());
        ReentrantLock lock = fileLockProvider.getLock(path);

        lock.lock();
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    Files.newOutputStream(path))) {
                oos.writeObject(userStatus);
                return userStatus;
            } catch (IOException e) {
                throw new RuntimeException(
                        "사용자 상태 저장에 실패했습니다.", e);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(UUID userStatusId) {
        Path path = fileFor(userStatusId);
        ReentrantLock lock = fileLockProvider.getLock(path);

        lock.lock();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(
                    "사용자 상태 삭제에 실패했습니다.", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(userStatus ->
                        userStatus.getUserId().equals(userId))
                .findFirst();
    }
}
