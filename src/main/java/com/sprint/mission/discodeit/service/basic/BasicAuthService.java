package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest dto) {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            if (dto.username().equals(user.getUsername()) && dto.password().equals(user.getPassword())) {
                return UserResponse.from(user, getUserStatus(user));
            }
        }
        throw new IllegalArgumentException("유저명 또는 패스워드가 잘못됨");
    }

    private UserStatus getUserStatus(User user) {
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 id(" + user.getId() + ")의 유저 상태를 찾을 수 없음"));
        return userStatus;
    }
}
