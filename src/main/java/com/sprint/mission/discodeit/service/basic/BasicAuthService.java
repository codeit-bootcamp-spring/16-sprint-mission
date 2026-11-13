package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User login(AuthLoginRequestDto dto) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(dto.getUsername()))
                .filter(u -> u.getPassword().equals(dto.getPassword()))
                .findFirst()
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.INVALID_LOGIN)
                );

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));

        userStatus.updateLastActiveAt();
        userStatusRepository.save(userStatus);

        return user;
    }
}
