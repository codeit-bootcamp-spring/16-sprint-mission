package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
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

    public ReadStatusResponseDto create(ReadStatusRequestDto readStatusRequestDto) {
        channelRepository.findById(readStatusRequestDto.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel can not found"));

        userRepository.findById(readStatusRequestDto.userId())
                .orElseThrow(() -> new NoSuchElementException("User can not found"));

        List<ReadStatus> statusList = readStatusRepository.findAllByUserId(readStatusRequestDto.userId());

        if (statusList.stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(readStatusRequestDto.channelId())) == true) {
            throw new IllegalArgumentException("This status already exist");
        }

        ReadStatus readStatus = new ReadStatus(readStatusRequestDto.userId(), readStatusRequestDto.channelId());
        readStatusRepository.save(readStatus.getChannelId(), readStatus.getUserId());
        return new ReadStatusResponseDto(readStatus.getId(), readStatus.getChannelId(), readStatus.getUserId());
    }

    public ReadStatusResponseDto find(UUID readStatusId) {
        ReadStatus readStatus =
                readStatusRepository.find(readStatusId).orElseThrow(() -> new NoSuchElementException("Status can not found"));
        return new ReadStatusResponseDto(readStatus.getId(), readStatus.getChannelId(), readStatus.getUserId());
    }

    public List<ReadStatusResponseDto> findAll(ReadStatusRequestDto readStatusRequestDto) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(readStatusRequestDto.channelId());
        List<ReadStatusResponseDto> readStatusResponseDtos = new ArrayList<>();
        for(ReadStatus readStatus : readStatuses) {
            readStatusResponseDtos.add(new ReadStatusResponseDto(readStatus.getId(), readStatus.getChannelId(), readStatus.getUserId()));
        }
        return readStatusResponseDtos;
    }

    public ReadStatusResponseDto update(UUID readStatusId, ReadStatusRequestDto readStatusRequestDto) {
        ReadStatus readStatus = readStatusRepository.find(readStatusId).orElseThrow(() -> new NoSuchElementException("ReadStatus can not found"));
        readStatusRepository.save(readStatusRequestDto.channelId(), readStatusRequestDto.userId());

        return new ReadStatusResponseDto(readStatus.getId(), readStatus.getChannelId(), readStatus.getUserId());
    }

    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.find(readStatusId).orElseThrow(() -> new NoSuchElementException("ReadStatus can not found"));
        readStatusRepository.deleteById(readStatusId);
    }
}
