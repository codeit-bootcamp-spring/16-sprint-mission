package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

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
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        if (readStatus == null) {
            throw new IllegalArgumentException("읽음 상태 정보는 필수입니다.");
        }

        if (readStatus.getId() == null) {
            throw new IllegalArgumentException("읽음 상태 ID는 필수입니다.");
        }

        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("읽음 상태 ID는 필수입니다.");
        }

        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        return data.values().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(
            UUID userId,
            UUID channelId
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        return data.values().stream()
                .filter(status ->
                        userId.equals(status.getUserId())
                                && channelId.equals(status.getChannelId())
                )
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("읽음 상태 ID는 필수입니다.");
        }

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("읽음 상태를 찾을 수 없습니다.");
        }

        data.remove(id);
    }
}