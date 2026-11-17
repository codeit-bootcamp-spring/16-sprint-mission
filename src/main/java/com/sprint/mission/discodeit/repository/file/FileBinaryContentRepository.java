package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {
  private final FileStore<BinaryContent> store;

  public FileBinaryContentRepository(
      @Value("${discodeit.repository.file-directory:data}") String directory) {
    this.store = new FileStore<>(directory, "binaryContents.ser");
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    Map<UUID, BinaryContent> data = store.load(); // 1. 읽기
    data.put(binaryContent.getId(), binaryContent); // 2. 넣기 (같은 id면 덮어쓰기)
    store.write(data); // 3. 쓰기
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.ofNullable(store.load().get(id));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    Map<UUID, BinaryContent> data = store.load();
    return ids.stream().map(data::get).filter(Objects::nonNull).toList();
  }

  @Override
  public void deleteById(UUID id) {
    Map<UUID, BinaryContent> data = store.load();
    data.remove(id);
    store.write(data);
  }
}
