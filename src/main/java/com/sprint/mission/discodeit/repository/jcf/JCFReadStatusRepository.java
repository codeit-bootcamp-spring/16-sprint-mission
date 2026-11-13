package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    name = "discodeit.repository.type",
    havingValue = "jcf",
    matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
  private final Map<UUID, ReadStatus> data = new HashMap<>();

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    data.put(readStatus.getId(), readStatus);
    return readStatus;
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    return data.values().stream()
        .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return data.values().stream().filter(rs -> rs.getUserId().equals(userId)).toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return data.values().stream().filter(rs -> rs.getChannelId().equals(channelId)).toList();
  }

  @Override
  public void deleteById(UUID id) {
    data.remove(id);
  }

  @Override
  public void deleteAllByUserId(UUID userId) {
    data.values().removeIf(rs -> rs.getUserId().equals(userId));
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    data.values().removeIf(rs -> rs.getChannelId().equals(channelId));
  }
}
