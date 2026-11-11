package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        ReadStatus existing = this.data.putIfAbsent(readStatus.getId(), readStatus);
        if (existing != null) throw new IllegalStateException("이미 저장된 메세지 확인 기록입니다.");
        return readStatus;
    }

    @Override
    public ReadStatus update(UUID id, ReadStatus readStatus) {
        ReadStatus existing = this.data.replace(id, readStatus);
        if (existing == null) throw new IllegalStateException("저장되지 않은 메세지 확인 기록입니다.");
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        ReadStatus readStatus = this.data.get(id);
        if (readStatus != null && !readStatus.isDeleted()) return Optional.of(readStatus);
        return Optional.empty();
    }

    @Override
    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        return this.data.values()
                .stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .filter(readStatus -> !readStatus.isDeleted())
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return this.data.values()
                .stream()
                .filter(readStatus -> !readStatus.isDeleted())
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return this.data.values()
                .stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .filter(readStatus -> !readStatus.isDeleted())
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return this.data.values()
                .stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .filter(readStatus -> !readStatus.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        ReadStatus readStatus = this.data.get(id);
        if (readStatus == null || readStatus.isDeleted())
            throw new IllegalArgumentException("삭제할 메세지 확인 기록이 존재하지 않습니다.");
        readStatus.softDelete();
    }
}
