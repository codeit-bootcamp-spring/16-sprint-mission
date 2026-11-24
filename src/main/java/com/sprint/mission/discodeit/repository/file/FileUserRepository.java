package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {

    private final Path userDirectory;
    private final FileLockProvider fileLockProvider;

    public FileUserRepository() {
        this(Path.of("data", "users"), new FileLockProvider());
    }

    public FileUserRepository(Path userDirectory) {
        this(userDirectory, new FileLockProvider());
    }

    public FileUserRepository(
            Path userDirectory,
            FileLockProvider fileLockProvider
    ) {
        this.userDirectory = userDirectory;
        this.fileLockProvider = fileLockProvider;

        try {
            Files.createDirectories(userDirectory);
        } catch (IOException e) {
            throw new RuntimeException("사용자 디렉터리 생성에 실패했습니다.", e);
        }
    }

    private Path fileFor(UUID userId) {
        return userDirectory.resolve(userId + ".ser");
    }

    @Override
    public Optional<User> findById(UUID userId) {
        Path file = fileFor(userId);
        ReentrantLock lock = fileLockProvider.getLock(file);

        lock.lock();
        try {
            if (Files.notExists(file)) {
                return Optional.empty();
            }

            return Optional.ofNullable(readUser(file));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<User> findAll() {
        List<User> allUsers = new ArrayList<>();

        try (Stream<Path> files = Files.list(userDirectory)) {
            files.filter(path ->
                            path.getFileName().toString().endsWith(".ser"))
                    .forEach(file -> {
                        ReentrantLock lock =
                                fileLockProvider.getLock(file);

                        lock.lock();
                        try {
                            if (Files.exists(file)) {
                                allUsers.add(readUser(file));
                            }
                        } finally {
                            lock.unlock();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("사용자 파일 목록을 읽는 데 실패했습니다.", e);
        }

        return allUsers;
    }

    private User readUser(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(
                Files.newInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("사용자 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public User save(User user) {
        Path file = fileFor(user.getId());
        ReentrantLock lock = fileLockProvider.getLock(file);

        lock.lock();
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(
                    Files.newOutputStream(file))) {
                out.writeObject(user);
                return user;
            } catch (IOException e) {
                throw new RuntimeException("사용자 파일 저장에 실패했습니다.", e);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(UUID userId) {
        Path file = fileFor(userId);
        ReentrantLock lock = fileLockProvider.getLock(file);

        lock.lock();
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("사용자 파일 삭제에 실패했습니다.", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, UUID userId) {
        return findAll().stream()
                .anyMatch(user ->
                        user.getUsername().equals(username)
                                && !user.getId().equals(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) {
            return false;
        }

        return findAll().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID userId) {
        if (email == null) {
            return false;
        }

        return findAll().stream()
                .anyMatch(user ->
                        email.equals(user.getEmail())
                                && !user.getId().equals(userId));
    }
}
