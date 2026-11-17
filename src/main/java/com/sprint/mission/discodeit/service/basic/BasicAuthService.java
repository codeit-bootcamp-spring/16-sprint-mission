package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthUserDto;
import com.sprint.mission.discodeit.exceptions.AuthenticationFailedException;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.dto.request.authlogin.AuthLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    // 로그인에 필요한 유저 정보를 조회하기 위해 UserRepository를 주입받는다.
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthUserDto login(AuthLoginRequest request) {

        // 전달받은 username과 일치하는 유저를 조회한다.
        User user = userRepository.findByUsername(request.username())
                // 일치하는 유저가 없다면 예외를 발생시킨다.
                .orElseThrow(() ->
                        new AuthenticationFailedException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new AuthenticationFailedException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 로그인에 성공한 유저 정보를 AuthUserDto로 변환하여 반환한다.
        // 비밀번호는 외부에 노출하지 않도록 DTO에 포함하지 않는다.
        return new AuthUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}