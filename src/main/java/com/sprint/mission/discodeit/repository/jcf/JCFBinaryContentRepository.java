package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> binaryContents = new ConcurrentHashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        binaryContents.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.copyOf(binaryContents.values());
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(binaryContents.get(id));
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        binaryContents.remove(binaryContent.getId());
    }
}