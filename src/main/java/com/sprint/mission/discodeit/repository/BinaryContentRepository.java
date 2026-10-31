package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    // 파일 저장
    BinaryContent save(BinaryContent binaryContent);

    // ID로 파일 하나 조회
    Optional<BinaryContent> findById(UUID id);

    // 전체 파일 조회
    List<BinaryContent> findAll();

    // 여러 ID로 파일 조회
    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    // 사용자 ID로 프로필 이미지 찾기
    Optional<BinaryContent> findByUserId(UUID userId);

    // 메시지 ID로 첨부 파일 찾기
    List<BinaryContent> findAllByMessageId(UUID messageId);

    // ID로 파일 존재 여부 확인
    boolean existsById(UUID id);

    // ID로 파일 삭제
    void deleteById(UUID id);
}
