package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path filePath = Path.of("data","readStatuses.ser");

    public FileReadStatusRepository() {
        try{
            Files.createDirectories(filePath.getParent());
        }catch (IOException e){
            throw new IllegalStateException("저장폴더 생성 못함",e);
        }
    }

    private void saveReadStatuses(List<ReadStatus> readStatuses) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
            out.writeObject(readStatuses);
        } catch (IOException e) {
            throw new IllegalStateException("읽음 상태 목록을 저장할 수 없습니다", e);
        }
    }

    private List<ReadStatus> loadReadStatuses() {
        if(!Files.exists(filePath)){
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            return (List<ReadStatus>) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("읽음 상태 목록을 읽을 수 없습니다",e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        List<ReadStatus> readStatuses = loadReadStatuses();
        readStatuses.removeIf(s -> s.getId().equals(readStatus.getId()));
        readStatuses.add(readStatus);
        saveReadStatuses(readStatuses);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return loadReadStatuses().stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return loadReadStatuses();
    }

    @Override
    public void deleteById(UUID id) {
        List<ReadStatus> readStatuses = loadReadStatuses();
        readStatuses.removeIf(s -> s.getId().equals(id));
        saveReadStatuses(readStatuses);
    }
}