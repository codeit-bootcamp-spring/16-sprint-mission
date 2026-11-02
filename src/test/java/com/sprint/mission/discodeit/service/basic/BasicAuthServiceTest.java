package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicAuthServiceTest {
    private UserRepository userRepository;
    private UserStatusRepository userStatusRepository;
    private BasicAuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = new JCFUserRepository();
        userStatusRepository = new JCFUserStatusRepository();
        authService = new BasicAuthService(userRepository, userStatusRepository);
    }

    private User saveUser(String username, String password) {
        User user = userRepository.save(new User(username, username + "@test.com", password));
        userStatusRepository.save(new UserStatus(user.getId()));
        return user;
    }

    @Test
    @DisplayName("유저명과 비밀번호가 일치하면 해당 유저 정보를 반환한다")
    void success() {
        User user = saveUser("tester", "password");

        UserResponse response = authService.login(new LoginRequest("tester", "password"));

        assertThat(response.userId()).isEqualTo(user.getId());
        assertThat(response.username()).isEqualTo("tester");
        assertThat(response.email()).isEqualTo(user.getEmail());
        assertThat(response.isOnline()).isTrue();
    }

    @Test
    @DisplayName("유저가 여러 명이어도 일치하는 유저를 찾아 반환한다")
    void findMatchingUserAmongMany() {
        saveUser("first", "password1");
        User second = saveUser("second", "password2");

        UserResponse response = authService.login(new LoginRequest("second", "password2"));

        assertThat(response.userId()).isEqualTo(second.getId());
    }

    @Test
    @DisplayName("비밀번호가 틀리면 예외가 발생한다")
    void wrongPassword() {
        saveUser("tester", "password");

        assertThatThrownBy(() -> authService.login(new LoginRequest("tester", "wrong")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 유저명이면 예외가 발생한다")
    void usernameNotFound() {
        saveUser("tester", "password");

        assertThatThrownBy(() -> authService.login(new LoginRequest("unknown", "password")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다른 유저의 비밀번호로는 로그인할 수 없다")
    void otherUsersPassword() {
        saveUser("first", "password1");
        saveUser("second", "password2");

        assertThatThrownBy(() -> authService.login(new LoginRequest("first", "password2")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("유저가 한 명도 없으면 예외가 발생한다")
    void noUsers() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("tester", "password")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("일치하는 유저의 UserStatus가 없으면 예외가 발생한다")
    void userStatusNotFound() {
        userRepository.save(new User("tester", "tester@test.com", "password"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("tester", "password")))
                .isInstanceOf(NoSuchElementException.class);
    }
}
