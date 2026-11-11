package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicUserStatusServiceTest {
    private UserRepository userRepository;
    private UserStatusRepository userStatusRepository;
    private BasicUserStatusService userStatusService;

    @BeforeEach
    void setUp() {
        userRepository = new JCFUserRepository();
        userStatusRepository = new JCFUserStatusRepository();
        userStatusService = new BasicUserStatusService(userRepository, userStatusRepository);
    }

    private User saveUser() {
        return userRepository.save(new User("tester", "tester@test.com", "password"));
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("유저가 존재하면 UserStatus를 생성하고 저장한다")
        void success() {
            User user = saveUser();

            UserStatusResponse response = userStatusService.create(new CreateUserStatusRequest(user.getId()));

            assertThat(response.userId()).isEqualTo(user.getId());
            assertThat(userStatusRepository.existsById(response.statusId())).isTrue();
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 예외가 발생한다")
        void userNotFound() {
            CreateUserStatusRequest request = new CreateUserStatusRequest(UUID.randomUUID());

            assertThatThrownBy(() -> userStatusService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("같은 유저의 UserStatus가 이미 존재하면 예외가 발생한다")
        void duplicated() {
            User user = saveUser();
            userStatusService.create(new CreateUserStatusRequest(user.getId()));

            assertThatThrownBy(() -> userStatusService.create(new CreateUserStatusRequest(user.getId())))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("find")
    class Find {
        @Test
        @DisplayName("id로 UserStatus를 조회한다")
        void success() {
            User user = saveUser();
            UserStatusResponse created = userStatusService.create(new CreateUserStatusRequest(user.getId()));

            UserStatusResponse found = userStatusService.find(created.statusId());

            assertThat(found.statusId()).isEqualTo(created.statusId());
            assertThat(found.userId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> userStatusService.find(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("모든 UserStatus를 조회한다")
        void success() {
            UserStatusResponse first = userStatusService.create(new CreateUserStatusRequest(saveUser().getId()));
            UserStatusResponse second = userStatusService.create(new CreateUserStatusRequest(saveUser().getId()));

            List<UserStatusResponse> result = userStatusService.findAll();

            assertThat(result).extracting(UserStatusResponse::statusId)
                    .containsExactlyInAnyOrder(first.statusId(), second.statusId());
        }

        @Test
        @DisplayName("UserStatus가 없으면 빈 리스트를 반환한다")
        void empty() {
            assertThat(userStatusService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("id로 찾은 UserStatus의 마지막 활동 시간을 수정한다")
        void success() {
            UserStatusResponse created = userStatusService.create(new CreateUserStatusRequest(saveUser().getId()));
            Instant newLastActiveAt = Instant.now().minusSeconds(600);

            UserStatusResponse updated = userStatusService.update(created.statusId(), new UpdateUserStatusRequest(newLastActiveAt));

            assertThat(updated.lastActiveAt()).isEqualTo(newLastActiveAt);
            assertThat(userStatusService.find(created.statusId()).lastActiveAt()).isEqualTo(newLastActiveAt);
        }

        @Test
        @DisplayName("수정할 값이 null이면 예외가 발생한다")
        void nullValue() {
            UserStatusResponse created = userStatusService.create(new CreateUserStatusRequest(saveUser().getId()));

            assertThatThrownBy(() -> userStatusService.update(created.statusId(), new UpdateUserStatusRequest(null)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            UpdateUserStatusRequest request = new UpdateUserStatusRequest(Instant.now());

            assertThatThrownBy(() -> userStatusService.update(UUID.randomUUID(), request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("updateByUserId")
    class UpdateByUserId {
        @Test
        @DisplayName("userId로 찾은 UserStatus의 마지막 활동 시간을 수정한다")
        void success() {
            User user = saveUser();
            UserStatusResponse created = userStatusService.create(new CreateUserStatusRequest(user.getId()));
            Instant newLastActiveAt = Instant.now().minusSeconds(600);

            UserStatusResponse updated = userStatusService.updateByUserId(user.getId(), new UpdateUserStatusRequest(newLastActiveAt));

            assertThat(updated.statusId()).isEqualTo(created.statusId());
            assertThat(updated.lastActiveAt()).isEqualTo(newLastActiveAt);
        }

        @Test
        @DisplayName("수정할 값이 null이면 예외가 발생한다")
        void nullValue() {
            User user = saveUser();
            userStatusService.create(new CreateUserStatusRequest(user.getId()));

            assertThatThrownBy(() -> userStatusService.updateByUserId(user.getId(), new UpdateUserStatusRequest(null)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 예외가 발생한다")
        void userNotFound() {
            UpdateUserStatusRequest request = new UpdateUserStatusRequest(Instant.now());

            assertThatThrownBy(() -> userStatusService.updateByUserId(UUID.randomUUID(), request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("유저는 있지만 UserStatus가 없으면 예외가 발생한다")
        void statusNotFound() {
            User user = saveUser();
            UpdateUserStatusRequest request = new UpdateUserStatusRequest(Instant.now());

            assertThatThrownBy(() -> userStatusService.updateByUserId(user.getId(), request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("매핑된 User가 없는 UserStatus는 삭제한다")
        void success() {
            UserStatus orphan = userStatusRepository.save(new UserStatus(UUID.randomUUID()));

            userStatusService.delete(orphan.getId());

            assertThat(userStatusRepository.existsById(orphan.getId())).isFalse();
        }

        @Test
        @DisplayName("매핑된 User가 존재하면 예외가 발생하고 UserStatus는 남는다")
        void userExists() {
            UserStatusResponse created = userStatusService.create(new CreateUserStatusRequest(saveUser().getId()));

            assertThatThrownBy(() -> userStatusService.delete(created.statusId()))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(userStatusRepository.existsById(created.statusId())).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> userStatusService.delete(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
