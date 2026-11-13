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
      @Value("${discodeit.repository.file-directory:data}") String directory,
      FileLockProvider lockProvider) {
    this.store = new FileStore<>(directory, "binaryContents.ser", lockProvider);
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    return store.update(data -> {
      data.put(binaryContent.getId(), binaryContent); // 2. 넣기 (같은 id면 덮어쓰기)
      return binaryContent;
    }); // 3. 쓰기
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
    store.update(data -> data.remove(id));
  }
}
