package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {
  private final FileStore<Channel> store;

  public FileChannelRepository(
      @Value("${discodeit.repository.file-directory:data}") String directory,
      FileLockProvider lockProvider) {
    this.store = new FileStore<>(directory, "channels.ser", lockProvider);
  }

  @Override
  public Channel save(Channel channel) {
    return store.update(data -> {
      data.put(channel.getId(), channel);
      return channel;
    });
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    return Optional.ofNullable(store.load().get(id));
  }

  @Override
  public List<Channel> findAll() {
    return new ArrayList<>(store.load().values());
  }

  @Override
  public boolean existsById(UUID id) {
    return store.load().values().stream().anyMatch(c -> c.getId().equals(id));
  }

  @Override
  public void deleteById(UUID id) {
    store.update(data -> data.remove(id));
  }
}
