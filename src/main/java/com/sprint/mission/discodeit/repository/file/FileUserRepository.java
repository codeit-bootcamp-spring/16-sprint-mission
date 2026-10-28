package com.sprint.mission.discodeit.repository.file;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileUserRepository implements UserRepository {
    final Path PATH = Paths.get("users.dat");
    Map<UUID, User> data = new ConcurrentHashMap<>();

    public FileUserRepository() {
        loadFile();
    }

    @Override
    public void save(User user) {
        if (user == null || user.getId() == null) {
            throw new NullPointerException("유저 데이터가 올바르지 않거나 ID가 없습니다.");
        }

        data.put(user.getId(), user);
        saveFile();
    }

    @Override
    public User findId(UUID id) {
        if (id == null) {
            throw new NullPointerException("조회하려는 유저 ID가 null입니다.");
        }

        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteId(UUID id) {
        if (id == null) {
            throw new NullPointerException("삭제하려는 유저 ID가 null입니다.");
        }
        data.remove(id);
        saveFile();
    }

    private void saveFile() {
        try (OutputStream os = Files.newOutputStream(PATH);
                ObjectOutputStream oos = new ObjectOutputStream(os))
         {
             oos.reset(); //직렬화 캐시 초기화
             oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("유저 파일 저장 중 치명적인 오류가 발생했습니다. 파일: " + PATH, e);
        }
    }

    private void loadFile() {
        if (!Files.exists(PATH)) return; // 파일이 아예 없으면 무시하고 빈 맵 사용

        try (InputStream is = Files.newInputStream(PATH);
                ObjectInputStream ois = new ObjectInputStream(is))
         {
            this.data = (Map<UUID, User>) ois.readObject(); // 파일에서 Map 통째로 복원
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 파일 로딩 중 치명적인 오류가 발생했습니다. 파일: " + PATH, e);
        }
    }
}



