package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(ReadStatusCreateDto dto);

    ReadStatusDto findById(UUID id);

    List<ReadStatusDto> findAllByUserId(UUID userId);

    ReadStatusDto update(ReadStatusUpdateDto dto);

    void delete(UUID id);
}
