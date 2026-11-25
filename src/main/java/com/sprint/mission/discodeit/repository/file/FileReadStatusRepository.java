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
      @Value("${discodeit.repository.file-directory:data}") String directory,
      FileLockProvider lockProvider) {
    this.store = new FileStore<>(directory, "readStatuses.ser", lockProvider);
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    return store.update(data -> {
      data.put(readStatus.getId(), readStatus); // 2. 넣기 (같은 id면 덮어쓰기)
      return readStatus;
    }); // 3. 쓰기
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
    store.update(data -> data.remove(id));
  }

  @Override
  public void deleteAllByUserId(UUID userId) {
    store.update(data -> data.values().removeIf(rs -> rs.getUserId().equals(userId)));
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    store.update(data -> data.values().removeIf(rs -> rs.getChannelId().equals(channelId)));
  }
}
