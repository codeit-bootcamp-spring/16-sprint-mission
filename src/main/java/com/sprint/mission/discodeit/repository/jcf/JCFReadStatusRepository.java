package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private final List<ReadStatus> readStatuses = new ArrayList<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        for (int i = 0; i < readStatuses.size(); i++) {
            if (readStatuses.get(i).getId().equals(readStatus.getId())) {
                readStatuses.set(i, readStatus);
                return readStatus;
            }
        }

        readStatuses.add(readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> findAll() {
        return List.copyOf(readStatuses);
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return readStatuses.stream()
                .filter(readStatus ->
                        readStatus.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatuses.stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return readStatuses.stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void delete(ReadStatus readStatus) {
        readStatuses.removeIf(existingReadStatus ->
                existingReadStatus.getId().equals(readStatus.getId()));
    }
}
