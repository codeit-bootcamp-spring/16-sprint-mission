package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {
  private final FileStore<Message> store;

  public FileMessageRepository(
      @Value("${discodeit.repository.file-directory:data}") String directory,
      FileLockProvider lockProvider) {
    this.store = new FileStore<>(directory, "messages.ser", lockProvider);
  }

  @Override
  public Message save(Message message) {
    return store.update(data -> {
      data.put(message.getId(), message);
      return message;
    });
  }

  @Override
  public Optional<Message> findById(UUID id) {
    return Optional.ofNullable(store.load().get(id));
  }

  @Override
  public List<Message> findAll() {
    return new ArrayList<>(store.load().values());
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return store.load().values().stream()
        .filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public List<Message> findAllByUserId(UUID userId) {
    return store.load().values().stream()
        .filter(message -> message.getAuthorId().equals(userId))
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return store.load().containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    store.update(data -> data.remove(id));
  }

  @Override
  public void deleteAllByUserId(UUID userId) {
    store.update(data -> data.values().removeIf(message -> message.getAuthorId().equals(userId)));
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    store.update(
        data -> data.values().removeIf(message -> message.getChannelId().equals(channelId)));
  }
}
