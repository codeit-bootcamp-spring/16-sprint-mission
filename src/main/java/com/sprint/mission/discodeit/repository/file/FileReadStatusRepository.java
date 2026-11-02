package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository extends FileRepository<ReadStatus> implements ReadStatusRepository {
    public FileReadStatusRepository() {
        super("./data/read_status.ser", new HashMap<>());
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> readStatusMap = this.readFile(filePath);
        ReadStatus existing = readStatusMap.putIfAbsent(readStatus.getId(), readStatus);
        if (existing != null) throw new IllegalStateException("이미 저장된 메세지 확인 기록입니다.");
        this.writeFile(readStatusMap, filePath);
        return readStatus;
    }

    @Override
    public ReadStatus update(UUID id, ReadStatus readStatus) {
        Map<UUID, ReadStatus> readStatusMap = this.readFile(filePath);
        ReadStatus existing = readStatusMap.replace(id, readStatus);
        if (existing == null) throw new IllegalStateException("저장되지 않은 메세지 확인 기록입니다.");
        this.writeFile(readStatusMap, filePath);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Map<UUID, ReadStatus> readStatusMap = this.readFile(filePath);
        ReadStatus readStatus = readStatusMap.get(id);
        if (readStatus != null && !readStatus.isDeleted()) return Optional.of(readStatus);
        return Optional.empty();
    }

    @Override
    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        Map<UUID, ReadStatus> readStatusMap = this.readFile(filePath);
        return readStatusMap.values()
                .stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .filter(readStatus -> !readStatus.isDeleted())
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        Map<UUID, ReadStatus> readStatusMap = this.readFile(filePath);
        return readStatusMap.values()
                .stream()
                .filter(readStatus -> !readStatus.isDeleted())
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> readStatusMap = this.readFile(filePath);
        return readStatusMap.values()
                .stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .filter(readStatus -> !readStatus.isDeleted())
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Map<UUID, ReadStatus> readStatusMap = this.readFile(filePath);
        return readStatusMap.values()
                .stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .filter(readStatus -> !readStatus.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, ReadStatus> readStatusMap = this.readFile(filePath);
        ReadStatus readStatus = readStatusMap.get(id);
        if (readStatus == null || readStatus.isDeleted())
            throw new IllegalArgumentException("삭제할 메세지 확인 기록이 존재하지 않습니다.");
        readStatus.softDelete();
        this.writeFile(readStatusMap, filePath);
    }
}
