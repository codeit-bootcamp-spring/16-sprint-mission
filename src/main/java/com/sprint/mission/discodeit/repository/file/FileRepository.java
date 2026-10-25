package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

public abstract class FileRepository<T extends BaseEntity> {
    public Map<UUID, T> readFile(String path) {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(path))) {
            @SuppressWarnings("unchecked")
            Map<UUID, T> data = (Map<UUID, T>) ois.readObject();
            return data;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError("직렬화된 객체의 클래스를 찾을 수 없습니다.");
        }
    }

    public void writeFile(Map<UUID, T> data, String path) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        }
    }

    public boolean checkIfFileExists(String pathStr) {
        Path path = Paths.get(pathStr);
        return Files.exists(path) && Files.isRegularFile(path);
    }
}
