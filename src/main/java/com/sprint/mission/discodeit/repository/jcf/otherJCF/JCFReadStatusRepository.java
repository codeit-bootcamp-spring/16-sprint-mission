package com.sprint.mission.discodeit.repository.jcf.otherJCF;

import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFReadStatusRepository implements ReadStatusRepository {

    // ReadStatus ID를 키로 사용하여 읽음 상태를 관리하는 Map
    private final Map<UUID, ReadStatus> data;

    // 읽음 상태를 저장할 HashMap을 초기화한다.
    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        // ReadStatus ID를 키로 사용하여 저장하거나 수정한다.
        data.put(readStatus.getId(), readStatus);

        // 저장하거나 수정한 ReadStatus를 반환한다.
        return readStatus;
    }

    // ID로 읽음 상태 조회
    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        // 유저 ID와 채널 ID가 모두 일치하는 읽음 상태를 찾아 반환한다.
        return data.values().stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId)
                                && readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        // 채널 ID가 일치하는 읽음 상태들을 새 목록으로 반환한다.
        return data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 특정 유저의 모든 읽음 상태 조회
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // ID로 읽음 상태 삭제
    @Override
    public void deleteById(UUID id) {
        if (data.remove(id) == null) {
            throw new IllegalArgumentException("삭제할 읽음 상태를 찾을 수 없습니다.");
        }
    }

    @Override
    public void deleteByUserIdAndChannelId(UUID userId, UUID channelId) {
        // 유저 ID와 채널 ID가 모두 일치하는 읽음 상태를 삭제한다.
        boolean removed = data.values().removeIf(
                readStatus -> readStatus.getUserId().equals(userId)
                        && readStatus.getChannelId().equals(channelId));

        // 삭제된 읽음 상태가 없으면 예외를 발생시킨다.
        if (!removed) {
            throw new IllegalArgumentException("삭제할 읽음 상태를 찾을 수 없습니다.");
        }
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        // 채널 ID가 일치하는 읽음 상태들을 삭제한다.
        data.values().removeIf(
                readStatus -> readStatus.getChannelId().equals(channelId));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        // 유저 ID가 일치하는 읽음 상태들을 삭제한다.
        boolean removed = data.values().removeIf(
                readStatus -> readStatus.getUserId().equals(userId));

        // 삭제된 읽음 상태가 없으면 예외를 발생시킨다.
        if (!removed) {
            throw new IllegalArgumentException("삭제할 읽음 상태를 찾을 수 없습니다.");
        }
    }
}