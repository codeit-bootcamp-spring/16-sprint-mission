package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> readStatuses = new HashMap<>();

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(readStatuses.get(readStatusId));
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatuses.values());
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatuses.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public void deleteById(UUID readStatusId) {
        readStatuses.remove(readStatusId);
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(
            UUID userId,
            UUID channelId
    ) {
        return readStatuses.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return readStatuses.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return readStatuses.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }
}