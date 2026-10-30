package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        return null;
    }

    @Override
    public UserStatusDto findById(UUID id) {
        return null;
    }

    @Override
    public List<UserStatusDto> findAll() {
        return List.of();
    }

    @Override
    public UserStatusDto update(UUID id, UserStatusUpdateRequest request) {
        return null;
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
