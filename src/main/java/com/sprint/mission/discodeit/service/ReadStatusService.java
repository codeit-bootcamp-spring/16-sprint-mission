package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusResponseDto createReadStatus(ReadStatusCreateRequestDto dto);

    ReadStatusResponseDto findReadStatus(UUID readStatusId);

    List<ReadStatusResponseDto> findAllReadStatusesByUserId(UUID userId);

    ReadStatusResponseDto updateReadStatus(UUID readStatusId);

    void deleteReadStatus(UUID readStatusId);
}
