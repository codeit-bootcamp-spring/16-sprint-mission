package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {
  private final FileStore<User> store;

  public FileUserRepository(
      @Value("${discodeit.repository.file-directory:data}") String directory,
      FileLockProvider lockProvider) {
    this.store = new FileStore<>(directory, "users.ser", lockProvider);
  }

  @Override
  public User save(User user) {
    return store.update(data -> {
      data.put(user.getId(), user); // 2. 넣기 (같은 id면 덮어쓰기)
      return user;
    }); // 3. 쓰기
  }

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(store.load().get(id)); // 없으면 빈 상자, 예외는 안 던짐
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return store.load().values().stream().filter(u -> u.getUsername().equals(username)).findFirst();
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(store.load().values());
  }

  @Override
  public boolean existsById(UUID id) {
    return store.load().containsKey(id);
  }

  @Override
  public boolean existsByUsername(String username) {
    return store.load().values().stream().anyMatch(u -> u.getUsername().equals(username));
  }

  @Override
  public boolean existsByEmail(String email) {
    return store.load().values().stream().anyMatch(u -> u.getEmail().equals(email));
  }

  @Override
  public void deleteById(UUID id) {
    store.update(data -> data.remove(id));
  }
}
