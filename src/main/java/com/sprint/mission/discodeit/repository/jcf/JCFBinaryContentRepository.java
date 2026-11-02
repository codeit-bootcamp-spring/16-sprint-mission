package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)

public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final List<BinaryContent> data;

    public JCFBinaryContentRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.removeIf(b -> b.getId().equals(binaryContent.getId()));
        data.add(binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return data.stream().filter(b -> b.getId().equals(id)).findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(b -> b.getId().equals(id));
    }
}