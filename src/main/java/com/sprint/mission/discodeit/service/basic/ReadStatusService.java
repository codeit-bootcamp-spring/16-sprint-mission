package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatusService(ReadStatusRepository readStatusRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.readStatusRepository = readStatusRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    private ReadStatusResponseDto toDto(ReadStatus readStatus) {
        return new ReadStatusResponseDto(readStatus.getId(), readStatus.getChannelId(), readStatus.getUserId());
    }

    public ReadStatusResponseDto create(ReadStatusRequestDto readStatusRequestDto) {
        channelRepository.findById(readStatusRequestDto.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel can not found"));

        userRepository.findById(readStatusRequestDto.userId())
                .orElseThrow(() -> new NoSuchElementException("User can not found"));

        List<ReadStatus> statusList = readStatusRepository.findAllByUserId(readStatusRequestDto.userId());

        if (statusList.stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(readStatusRequestDto.channelId()))) {
            throw new IllegalArgumentException("This status already exist");
        }

        ReadStatus readStatus = new ReadStatus(readStatusRequestDto.userId(), readStatusRequestDto.channelId());

        return toDto(readStatusRepository.save(readStatus));
    }

    public ReadStatusResponseDto find(UUID readStatusId) {
        ReadStatus readStatus =
                readStatusRepository.find(readStatusId).orElseThrow(() -> new NoSuchElementException("Status can not found"));
        return toDto(readStatus);
    }

    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public ReadStatusResponseDto update(UUID readStatusId, ReadStatusUpdateDto readStatusUpdateDto) {
        ReadStatus readStatus = readStatusRepository.find(readStatusId).orElseThrow(() -> new NoSuchElementException("ReadStatus can not found"));
        readStatus.updateLastReadAt(readStatusUpdateDto.lastReadAt());
        readStatusRepository.save(readStatus);

        return toDto(readStatus);
    }

    public void delete(UUID readStatusId) {
        readStatusRepository.find(readStatusId).orElseThrow(() -> new NoSuchElementException("ReadStatus can not found"));
        readStatusRepository.deleteById(readStatusId);
    }
}
