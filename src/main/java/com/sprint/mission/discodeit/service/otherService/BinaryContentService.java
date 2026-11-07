package com.sprint.mission.discodeit.service.other;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    // 바이너리 콘텐츠 생성
    BinaryContent create(BinaryContentCreateRequest request);

    // ID로 단건 조회
    BinaryContent find(UUID id);

    // ID 목록으로 조회
    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    // ID로 삭제
    void delete(UUID id);
}
