package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository extends FileRepository<BinaryContent> implements BinaryContentRepository {
    public FileBinaryContentRepository() {
        super("binary_content.ser", new HashMap<>());
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Map<UUID, BinaryContent> binaryContentMap = this.readFile(resolvedPath);
        BinaryContent existing = binaryContentMap.putIfAbsent(binaryContent.getId(), binaryContent);
        if (existing != null) throw new IllegalStateException("이미 저장된 바이너리 데이터입니다.");
        this.writeFile(binaryContentMap, resolvedPath);
        return binaryContent;
    }

    @Override
    public BinaryContent update(UUID id, BinaryContent binaryContent) {
        Map<UUID, BinaryContent> binaryContentMap = this.readFile(resolvedPath);
        BinaryContent existing = binaryContentMap.replace(id, binaryContent);
        if (existing == null) throw new IllegalStateException("저장되지 않은 바이너리 데이터입니다.");
        this.writeFile(binaryContentMap, resolvedPath);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Map<UUID, BinaryContent> binaryContentMap = this.readFile(resolvedPath);
        BinaryContent binaryContent = binaryContentMap.get(id);
        if (binaryContent != null && !binaryContent.isDeleted()) return Optional.of(binaryContent);
        return Optional.empty();
    }

    @Override
    public List<BinaryContent> findAll() {
        Map<UUID, BinaryContent> binaryContentMap = this.readFile(resolvedPath);
        return binaryContentMap.values()
                .stream()
                .filter(binaryContent -> !binaryContent.isDeleted())
                .toList();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        Map<UUID, BinaryContent> binaryContentMap = this.readFile(resolvedPath);
        return binaryContentMap.values()
                .stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .filter(binaryContent -> !binaryContent.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, BinaryContent> binaryContentMap = this.readFile(resolvedPath);
        BinaryContent binaryContent = binaryContentMap.get(id);
        if (binaryContent == null || binaryContent.isDeleted())
            throw new IllegalArgumentException("삭제할 바이너리 데이터가 존재하지 않습니다.");
        binaryContent.softDelete();
        this.writeFile(binaryContentMap, resolvedPath);
    }
}
