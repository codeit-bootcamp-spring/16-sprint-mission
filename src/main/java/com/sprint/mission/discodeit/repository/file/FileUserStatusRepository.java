package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path filePath;

    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory}") String directory) {
        this.filePath = Path.of(directory,"userStatuses.ser");

        try{
            Files.createDirectories(filePath.getParent());
        }catch (IOException e){
            throw new IllegalStateException("저장폴더 생성 못함",e);
        }
    }

    private void saveUserStatuses(List<UserStatus> userStatuses) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
            out.writeObject(userStatuses);
        } catch (IOException e) {
            throw new IllegalStateException("접속 상태 목록을 저장할 수 없습니다", e);
        }
    }

    private List<UserStatus> loadUserStatuses() {
        if(!Files.exists(filePath)){
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            return (List<UserStatus>) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("접속 상태 목록을 읽을 수 없습니다",e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        List<UserStatus> userStatuses = loadUserStatuses();
        userStatuses.removeIf(s -> s.getId().equals(userStatus.getId()));
        userStatuses.add(userStatus);
        saveUserStatuses(userStatuses);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return loadUserStatuses().stream().filter(s ->
                s.getId().equals(id)).findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return loadUserStatuses();
    }

    @Override
    public void deleteById(UUID id) {
        List<UserStatus> userStatuses = loadUserStatuses();
        userStatuses.removeIf(s -> s.getId().equals(id));
        saveUserStatuses(userStatuses);
    }
}