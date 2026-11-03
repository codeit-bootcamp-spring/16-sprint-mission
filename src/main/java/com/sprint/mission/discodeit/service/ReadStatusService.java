package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatusCreateDto dto);
    ReadStatus find(UUID readStatusId);
    List<ReadStatus> findAll();
    ReadStatus update(ReadStatusUpdateDto dto);
    void delete(UUID readStatusId);
}
