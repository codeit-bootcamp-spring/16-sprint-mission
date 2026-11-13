package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {
  private final FileStore<UserStatus> store;

  public FileUserStatusRepository(
      @Value("${discodeit.repository.file-directory:data}") String directory,
      FileLockProvider lockProvider) {
    this.store = new FileStore<>(directory, "userStatuses.ser", lockProvider);
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    return store.update(data -> {
      data.put(userStatus.getId(), userStatus); // 2. 넣기 (같은 id면 덮어쓰기)
      return userStatus;
    }); // 3. 쓰기
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    return Optional.ofNullable(store.load().get(id));
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return store.load().values().stream().filter(us -> us.getUserId().equals(userId)).findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    return new ArrayList<>(store.load().values());
  }

  @Override
  public void deleteById(UUID id) {
    store.update(data -> data.remove(id));
  }

  @Override
  public void deleteByUserId(UUID userId) {
    store.update(data -> data.values().removeIf(us -> us.getUserId().equals(userId)));
  }
}
