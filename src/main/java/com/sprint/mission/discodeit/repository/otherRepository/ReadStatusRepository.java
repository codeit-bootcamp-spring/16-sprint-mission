package com.sprint.mission.discodeit.repository.otherRepository;

import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    // 생성 및 수정
    ReadStatus save(ReadStatus readStatus);

    // ID로 읽음 상태 조회
    Optional<ReadStatus> findById(UUID id);

    // 특정 채널의 모든 읽음 상태 조회
    List<ReadStatus> findAllByChannelId(UUID channelId);

    // 특정 유저의 모든 읽음 상태 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 특정 유저의 특정 채널 읽음 상태 조회
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    // ID로 읽음 상태 삭제
    void deleteById(UUID id);

    // 특정 채널의 모든 읽음 상태 삭제
    void deleteByChannelId(UUID channelId);

    // 특정 유저의 모든 읽음 상태 삭제
    void deleteByUserId(UUID userId);

    // 특정 유저의 특정 채널 읽음 상태 삭제
    void deleteByUserIdAndChannelId(UUID userId, UUID channelId);
}