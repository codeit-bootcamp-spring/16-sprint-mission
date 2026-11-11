package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto login(LoginRequestDto dto){
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(dto.username()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 이름입니다: " + dto.username()));
        if(!user.getPassword().equals(dto.password())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        return UserResponseDto.excludePassword(user,null );
    }
}
