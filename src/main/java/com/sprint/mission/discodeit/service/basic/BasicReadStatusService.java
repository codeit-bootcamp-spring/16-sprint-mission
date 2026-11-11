package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateDto dto) {
        //관련된 Channel이나 User가 존재하지 않으면 예외 발생
        if (!userRepository.existsById(dto.userId())) {
            throw new NoSuchElementException("User not found with id " + dto.userId());
        }
        if (!channelRepository.existsById(dto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + dto.channelId());
        }
        // 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외 발생
        boolean alreadyExists = readStatusRepository.findByUserIdAndChannelId(dto.userId(), dto.channelId()).isPresent();
        if (alreadyExists) {
            throw new IllegalArgumentException("해당 유저와 채널에 대한 읽음 상태가 이미 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(dto.userId(), dto.channelId(), dto.lastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatus> findAll() {
        return readStatusRepository.findAll();
    }

    @Override
    public ReadStatus update(ReadStatusUpdateDto dto) {
        ReadStatus readStatus = readStatusRepository.findById(dto.id())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + dto.id() + " not found"));

        readStatus.updateLastReadAt(dto.lastReadAt());
        return readStatusRepository.save(readStatus);
    }
    @Override
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

        readStatusRepository.delete(readStatus);
    }
}




