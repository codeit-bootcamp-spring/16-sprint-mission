package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data;

    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        BinaryContent existing = this.data.putIfAbsent(binaryContent.getId(), binaryContent);
        if (existing != null) throw new IllegalStateException("이미 저장된 바이너리 데이터입니다.");
        return binaryContent;
    }

    @Override
    public BinaryContent update(UUID id, BinaryContent binaryContent) {
        BinaryContent existing = this.data.replace(id, binaryContent);
        if (existing == null) throw new IllegalStateException("저장되지 않은 바이너리 데이터입니다.");
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        BinaryContent binaryContent = this.data.get(id);
        if (binaryContent != null && !binaryContent.isDeleted()) return Optional.of(binaryContent);
        return Optional.empty();
    }

    @Override
    public List<BinaryContent> findAll() {
        return this.data.values()
                .stream()
                .filter(binaryContent -> !binaryContent.isDeleted())
                .toList();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return this.data.values()
                .stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .filter(binaryContent -> !binaryContent.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        BinaryContent binaryContent = this.data.get(id);
        if (binaryContent == null || binaryContent.isDeleted())
            throw new IllegalArgumentException("삭제할 바이너리 데이터가 존재하지 않습니다.");
        binaryContent.softDelete();
    }
}
