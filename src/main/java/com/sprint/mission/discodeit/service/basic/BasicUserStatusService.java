package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final CommonValidator validator;

    @Override
    public void updateByUserId(UUID userId, Instant lastActiveAt) {
        validator.validateNotNull(
                userId,
                "사용자 ID는 null일 수 없습니다."
        );
        validator.validateNotNull(
                lastActiveAt,
                "마지막 활동 시간은 null일 수 없습니다."
        );

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "사용자 상태 정보가 존재하지 않습니다."
                ));

        userStatus.updateLastActiveAt(lastActiveAt);
        userStatusRepository.save(userStatus);
    }
}