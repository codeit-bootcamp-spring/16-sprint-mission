package com.sprint.mission.discodeit.repository.jcf.otherJCF;

import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    // BinaryContent ID를 키로 사용하여 바이너리 콘텐츠를 관리하는 Map
    private final Map<UUID, BinaryContent> data;

    // 바이너리 콘텐츠를 저장할 HashMap을 초기화한다.
    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        // BinaryContent ID를 키로 사용하여 저장하거나 수정한다.
        data.put(binaryContent.getId(), binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        // ID가 일치하는 BinaryContent를 찾아 반환한다.
        // 일치하는 데이터가 없으면 Optional.empty()를 반환한다.
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        // ID가 일치하는 BinaryContent를 삭제한다.
        BinaryContent removed = data.remove(id);

        // 삭제된 데이터가 없으면 예외를 발생시킨다.
        if (removed == null) {
            throw new IllegalArgumentException("삭제할 BinaryContent를 찾을 수 없습니다.");
        }
    }
}