package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFReadStatusRepository implements ReadStatusRepository {
    @Override
    public ReadStatus save(ReadStatus readStatus) {
        return null;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteAllByUserId(UUID userId) {

    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {

    }
}
