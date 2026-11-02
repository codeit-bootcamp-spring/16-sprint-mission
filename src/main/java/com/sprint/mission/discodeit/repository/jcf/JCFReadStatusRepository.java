package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final List<ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.removeIf(s -> s.getId().equals(readStatus.getId()));
        data.add(readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return data.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(s -> s.getId().equals(id));
    }
}