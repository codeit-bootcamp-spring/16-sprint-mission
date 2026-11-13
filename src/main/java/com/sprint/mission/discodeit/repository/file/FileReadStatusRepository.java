package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {
  private final FileStore<ReadStatus> store;

  public FileReadStatusRepository(
      @Value("${discodeit.repository.file-directory:data}") String directory) {
    this.store = new FileStore<>(directory, "readStatuses.ser");
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    Map<UUID, ReadStatus> data = store.load(); // 1. 읽기
    data.put(readStatus.getId(), readStatus); // 2. 넣기 (같은 id면 덮어쓰기)
    store.write(data); // 3. 쓰기
    return readStatus;
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return Optional.ofNullable(store.load().get(id));
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    return store.load().values().stream()
        .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return store.load().values().stream().filter(rs -> rs.getUserId().equals(userId)).toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return store.load().values().stream()
        .filter(rs -> rs.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    Map<UUID, ReadStatus> data = store.load();
    data.remove(id);
    store.write(data);
  }

  @Override
  public void deleteAllByUserId(UUID userId) {
    Map<UUID, ReadStatus> data = store.load();
    data.values().removeIf(rs -> rs.getUserId().equals(userId));
    store.write(data);
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    Map<UUID, ReadStatus> data = store.load();
    data.values().removeIf(rs -> rs.getChannelId().equals(channelId));
    store.write(data);
  }
}
