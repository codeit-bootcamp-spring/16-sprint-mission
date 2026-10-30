package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus entity);

    ReadStatus findById(UUID id);

    List<ReadStatus> findAll();

    void delete(UUID id);

    default ReadStatus findByUserIdAndChannelId(
            UUID userId,
            UUID channelId
    ) {
        return findAll().stream()
                .filter(status ->
                        status.getUserId().equals(userId)
                                && status.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    default List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(status ->
                        status.getUserId().equals(userId))
                .toList();
    }

    default List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(status ->
                        status.getChannelId().equals(channelId))
                .toList();
    }
}