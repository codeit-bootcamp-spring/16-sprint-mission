package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserStatusRepository implements UserStatusRepository {

    private final Path directory;

    public FileUserStatusRepository() {
        this(Path.of(System.getProperty("user.dir"), "data", "userStatuses"));
    }

    public FileUserStatusRepository(Path directory) {
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("사용자 상태 저장 폴더 생성에 실패했습니다.", e);
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
            throw new RuntimeException("사용자 상태 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        Path path = fileFor(userStatusId);

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        return Optional.of(readUserStatus(path));
    }

    @Override
    public List<UserStatus> findAll() {
        try (var paths = Files.list(directory)) {
            return paths
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::readUserStatus)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("사용자 상태 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = fileFor(userStatus.getId());

        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(path))) {
            oos.writeObject(userStatus);
            return userStatus;
        } catch (IOException e) {
            throw new RuntimeException("사용자 상태 저장에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteById(UUID userStatusId) {
        Path path = fileFor(userStatusId);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("사용자 상태 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }
}