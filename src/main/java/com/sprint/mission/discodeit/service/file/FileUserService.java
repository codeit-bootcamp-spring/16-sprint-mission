
package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// 파일 입출력을 사용해 사용자 데이터를 관리하는 서비스
public class FileUserService implements UserService {

    // 사용자 데이터를 저장할 파일 경로
    private final Path filePath = Paths.get("data", "users.dat");

    // 사용자 데이터를 메모리에서 관리할 공간
    private final Map<UUID, User> data;

    // 생성자: 저장된 사용자 데이터를 파일에서 불러오기
    public FileUserService() {
        this.data = loadData();
    }

    // 파일에서 사용자 데이터 불러오기
    private Map<UUID, User> loadData() {
        try {
            if (!Files.exists(filePath)) {
                return new HashMap<>();
            }

            try (ObjectInputStream input = new ObjectInputStream(
                    Files.newInputStream(filePath))) {
                return (Map<UUID, User>) input.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("사용자 데이터를 불러오지 못했습니다.", e);
        }
    }

    // 현재 사용자 데이터를 파일에 저장하기
    private void saveData() {
        try {
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream output = new ObjectOutputStream(
                    Files.newOutputStream(filePath))) {
                output.writeObject(data);
            }

        } catch (IOException e) {
            throw new IllegalStateException("사용자 데이터를 저장하지 못했습니다.", e);
        }
    }

    // 사용자 생성
    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자는 null일 수 없습니다.");
        }
        if (user.getUsername() == null) {
            throw new IllegalArgumentException("사용자 이름은 null일 수 없습니다.");
        }

        data.put(user.getId(), user);
        saveData();

        return user;
    }

    // ID로 사용자 조회
    @Override
    public User findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        return data.get(id);
    }

    // 모든 사용자 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    // 사용자 수정
    @Override
    public User update(UUID id, User user) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }
        if (user == null) {
            throw new IllegalArgumentException("사용자는 null일 수 없습니다.");
        }
        if (user.getUsername() == null) {
            throw new IllegalArgumentException("사용자 이름은 null일 수 없습니다.");
        }

        User existingUser = data.get(id);

        if (existingUser == null) {
            throw new IllegalArgumentException("수정할 사용자가 존재하지 않습니다.");
        }

        existingUser.update(user.getUsername());
        saveData();

        return existingUser;
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("삭제할 사용자가 존재하지 않습니다.");
        }

        data.remove(id);
        saveData();
    }
}
