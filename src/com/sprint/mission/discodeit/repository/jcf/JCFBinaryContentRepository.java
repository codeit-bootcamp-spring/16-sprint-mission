package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        if (binaryContent == null) {
            throw new IllegalArgumentException("첨부파일 정보는 필수입니다.");
        }

        if (binaryContent.getId() == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> result = new ArrayList<>();

        for (UUID id : ids) {
            if (id == null) {
                throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
            }

            BinaryContent content = data.get(id);

            if (content != null) {
                result.add(content);
            }
        }

        return result;
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("첨부파일 정보를 찾을 수 없습니다.");
        }

        data.remove(id);
    }
}