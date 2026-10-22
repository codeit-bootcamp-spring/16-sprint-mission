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
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {

    private final Path userDirectory;

    public FileUserRepository() {
        this(Path.of("data", "users"));
    }

    public FileUserRepository(Path userDirectory) {
        this.userDirectory = userDirectory;

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
    public User findById(UUID userId) {
        Path file = fileFor(userId);

        if (Files.notExists(file)) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("사용자 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (Stream<Path> files = Files.list(userDirectory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .forEach(file -> users.add(readUser(file)));
        } catch (IOException e) {
            throw new RuntimeException("사용자 파일 목록을 읽는 데 실패했습니다.", e);
        }

        return users;
    }

    private User readUser(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("사용자 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public User save(User user) {
        Path file = fileFor(user.getId());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(user);
            return user;
        } catch (IOException e) {
            throw new RuntimeException("사용자 파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteById(UUID userId) {
        try {
            Files.deleteIfExists(fileFor(userId));
        } catch (IOException e) {
            throw new RuntimeException("사용자 파일 삭제에 실패했습니다.", e);
        }
    }
}