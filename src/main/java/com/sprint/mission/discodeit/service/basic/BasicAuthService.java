
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.AuthLoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        if (request == null
                || request.username() == null
                || request.password() == null) {
            throw new IllegalArgumentException("아이디와 비밀번호를 입력하세요.");
        }

        User user = userRepository.findByUsername(request.username())
                .filter(found -> found.getPassword() != null
                        && found.getPassword().equals(request.password()))
                .orElseThrow(() ->
                        new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        return new AuthLoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId()
        );
    }
}
