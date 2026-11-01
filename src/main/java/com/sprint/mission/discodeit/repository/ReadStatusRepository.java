package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    public ReadStatus save(UUID channelId, UUID userId);
    public Optional<ReadStatus> find(UUID readStatusId);
    public List<ReadStatus> findAllByUserId(UUID userId);
    public List<ReadStatus> findAllByChannelId(UUID channelId);
    public void deleteById(UUID id);
}
