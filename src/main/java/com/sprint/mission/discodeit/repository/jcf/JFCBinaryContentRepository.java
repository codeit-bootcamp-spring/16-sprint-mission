package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JFCBinaryContentRepository implements BinaryContentRepository {
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        return null;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }
}
