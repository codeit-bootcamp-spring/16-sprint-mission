package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;

public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data =new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        this.data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<BinaryContent> findByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return ids.stream()
                .map(this.data::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        if (binaryContent != null) {
            this.data.remove(binaryContent.getId());
        }
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
