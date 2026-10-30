package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserService {

    private final Path directory;

    public FileUserService() {
        this(Paths.get(System.getProperty("user.dir"), "data", "users"));
    }

    /** 테스트 또는 실행 코드에서 저장 폴더를 직접 지정할 수 있습니다. */
    public FileUserService(Path directory) {
        this.directory = directory;
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("사용자 저장 디렉터리를 만들 수 없습니다: " + directory, e);
        }
    }

    public void create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        Path file = fileFor(user.getId());
        if (Files.exists(file)) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + user.getId());
        }

        validateEmail(user.getEmail());
        if (isEmailDuplicate(user.getEmail(), null)) {
            throw new IllegalArgumentException("Email is already in use: " + user.getEmail());
        }

        write(file, user);
    }

    public User findById(UUID id) {
        Path file = fileFor(id);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Not Found user id: " + id);
        }
        return read(file);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (var files = Files.list(directory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> users.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException("사용자 파일 목록을 읽을 수 없습니다: " + directory, e);
        }

        return List.copyOf(users);
    }

    public void update(UUID id, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        User existingUser = findById(id);
        validateEmail(user.getEmail());
        if (isEmailDuplicate(user.getEmail(), id)) {
            throw new IllegalArgumentException("Email is already in use: " + user.getEmail());
        }

        existingUser.update(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                existingUser.getProfileId()
        );
        write(fileFor(id), existingUser);
    }

    public void delete(UUID id) {
        try {
            if (!Files.deleteIfExists(fileFor(id))) {
                throw new IllegalArgumentException("Not Found user id: " + id);
            }
        } catch (IOException e) {
            throw new IllegalStateException("사용자 파일을 삭제할 수 없습니다: " + id, e);
        }
    }

    private boolean isEmailDuplicate(String email, UUID excludeUserId) {
        return findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)
                        && !user.getId().equals(excludeUserId));
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }

        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다: " + email);
        }
    }

    /** UUID를 파일명으로 사용해 저장·조회·삭제에서 공통으로 쓸 경로를 만듭니다. */
    private Path fileFor(UUID id) {
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, User user) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(user);
        } catch (IOException e) {
            throw new IllegalStateException("사용자 파일을 저장할 수 없습니다: " + file, e);
        }
    }

    private User read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("사용자 파일을 읽을 수 없습니다: " + file, e);
        }
    }
}
