package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final List<BinaryContent> binaryContents = new ArrayList<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        for (int i = 0; i < binaryContents.size(); i++) {
            if (binaryContents.get(i).getId().equals(binaryContent.getId())) {
                binaryContents.set(i, binaryContent);
                return binaryContent;
            }
        }

        binaryContents.add(binaryContent);
        return binaryContent;
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.copyOf(binaryContents);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return binaryContents.stream()
                .filter(binaryContent ->
                        binaryContent.getId().equals(id))
                .findFirst();
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        binaryContents.removeIf(existingBinaryContent ->
                existingBinaryContent.getId().equals(binaryContent.getId()));
    }
}
