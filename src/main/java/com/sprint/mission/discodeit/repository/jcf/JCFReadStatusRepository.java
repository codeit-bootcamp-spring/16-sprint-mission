package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> saveAll(List<ReadStatus> readStatusList) {
        readStatusList.forEach(readStatus -> this.data.put(readStatus.getId(), readStatus));
        return readStatusList;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(this.data.get(readStatusId));
    }

    /* 이 메서드가 왜 있는 지 모르겠음
    public Optional<ReadStatus> findByUserId(UUID userId) {
        return Optional.ofNullable(this.data.get(userId));
    }
     */

    @Override
    public List<ReadStatus> findAll() {
        return this.data.values().stream().toList();
    }

    // 구현체
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    @Override
    public boolean existsById(UUID readStatusId) {
        return this.data.containsKey(readStatusId);
    }

    @Override
    public void deleteById(UUID readStatusId) {
       this.data.remove(readStatusId);
    }
}
