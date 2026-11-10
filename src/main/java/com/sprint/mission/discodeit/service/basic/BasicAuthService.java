package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final CommonValidator validator;

    @Override
    public UserDto login(LoginDto dto) {
        validator.validateNotNull(
                dto,
                "로그인 정보는 null일 수 없습니다."
        );
        validator.validateNotBlank(
                dto.username(),
                "사용자 이름은 비어있을 수 없습니다."
        );
        validator.validateNotBlank(
                dto.password(),
                "사용자 비밀번호는 비어있을 수 없습니다."
        );

        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자가 존재하지 않습니다."
                        )
                );
        if (!user.getPassword().equals(dto.password())) {
            throw new IllegalArgumentException(
                    "비밀번호가 일치하지 않습니다."
            );
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자 상태 정보가 존재하지 않습니다."
                        )
                );
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getAge(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.isOnline()
        );
    }
}
