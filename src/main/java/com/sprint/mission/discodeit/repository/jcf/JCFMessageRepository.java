package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    name = "discodeit.repository.type",
    havingValue = "jcf",
    matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {
  private final Map<UUID, Message> data = new HashMap<>();

  @Override
  public Message save(Message message) {
    data.put(message.getId(), message);
    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public List<Message> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return data.values().stream().filter(m -> m.getChannelId().equals(channelId)).toList();
  }

  @Override
  public List<Message> findAllByUserId(UUID userId) {
    return data.values().stream().filter(m -> m.getAuthorId().equals(userId)).toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return data.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    data.remove(id);
  }

  @Override
  public void deleteAllByUserId(UUID userId) {
    data.values().removeIf(m -> m.getAuthorId().equals(userId));
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    data.values().removeIf(m -> m.getChannelId().equals(channelId));
  }
}
