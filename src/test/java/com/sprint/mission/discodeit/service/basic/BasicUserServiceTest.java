package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicUserServiceTest {
    private UserRepository userRepository;
    private UserStatusRepository userStatusRepository;
    private BinaryContentRepository binaryContentRepository;
    private ReadStatusRepository readStatusRepository;
    private BasicUserService userService;

    @BeforeEach
    void setUp() {
        userRepository = new JCFUserRepository();
        userStatusRepository = new JCFUserStatusRepository();
        binaryContentRepository = new JCFBinaryContentRepository();
        readStatusRepository = new JCFReadStatusRepository();
        userService = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository, readStatusRepository);
    }

    private BinaryContentRequest profileRequest() {
        byte[] bytes = {1, 2, 3};
        return new BinaryContentRequest("profile.png", (long) bytes.length, "image/png", bytes);
    }

    private UserResponse createUser(String username, String email) {
        return userService.create(new CreateUserRequest(username, email, "password", null));
    }

    private UUID profileIdOf(UUID userId) {
        return userRepository.findById(userId).orElseThrow().getProfileId();
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("유저를 생성하면 UserStatus도 함께 생성되고 온라인 상태다")
        void success() {
            UserResponse response = createUser("tester", "tester@test.com");

            assertThat(response.username()).isEqualTo("tester");
            assertThat(response.email()).isEqualTo("tester@test.com");
            assertThat(response.isOnline()).isTrue();
            assertThat(userRepository.existsById(response.userId())).isTrue();
            assertThat(userStatusRepository.findByUserId(response.userId())).isPresent();
        }

        @Test
        @DisplayName("프로필을 함께 보내면 BinaryContent가 저장되고 유저와 연결된다")
        void withProfile() {
            UserResponse response = userService.create(
                    new CreateUserRequest("tester", "tester@test.com", "password", profileRequest()));

            UUID profileId = profileIdOf(response.userId());
            assertThat(profileId).isNotNull();
            assertThat(binaryContentRepository.existsById(profileId)).isTrue();
        }

        @Test
        @DisplayName("프로필 없이 생성하면 프로필 id가 없다")
        void withoutProfile() {
            UserResponse response = createUser("tester", "tester@test.com");

            assertThat(profileIdOf(response.userId())).isNull();
        }

        @Test
        @DisplayName("유저명이 중복되면 예외가 발생한다")
        void duplicatedUsername() {
            createUser("tester", "tester@test.com");

            assertThatThrownBy(() -> createUser("tester", "other@test.com"))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("이메일이 중복되면 예외가 발생한다")
        void duplicatedEmail() {
            createUser("tester", "tester@test.com");

            assertThatThrownBy(() -> createUser("other", "tester@test.com"))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("find")
    class Find {
        @Test
        @DisplayName("id로 유저를 조회한다")
        void success() {
            UserResponse created = createUser("tester", "tester@test.com");

            UserResponse found = userService.find(created.userId());

            assertThat(found.userId()).isEqualTo(created.userId());
            assertThat(found.username()).isEqualTo("tester");
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> userService.find(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("모든 유저를 조회한다")
        void success() {
            UserResponse first = createUser("first", "first@test.com");
            UserResponse second = createUser("second", "second@test.com");

            List<UserResponse> result = userService.findAll();

            assertThat(result).extracting(UserResponse::userId)
                    .containsExactlyInAnyOrder(first.userId(), second.userId());
        }

        @Test
        @DisplayName("유저가 없으면 빈 리스트를 반환한다")
        void empty() {
            assertThat(userService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("보낸 값만 수정하고 null인 필드는 그대로 둔다")
        void success() {
            UserResponse created = createUser("tester", "tester@test.com");

            UserResponse updated = userService.update(created.userId(),
                    new UpdateUserRequest("newName", null, null, null));

            assertThat(updated.username()).isEqualTo("newName");
            assertThat(updated.email()).isEqualTo("tester@test.com");
        }

        @Test
        @DisplayName("자기 자신의 기존 유저명으로 수정해도 중복으로 보지 않는다")
        void sameUsernameAsSelf() {
            UserResponse created = createUser("tester", "tester@test.com");

            UserResponse updated = userService.update(created.userId(),
                    new UpdateUserRequest("tester", null, null, null));

            assertThat(updated.username()).isEqualTo("tester");
        }

        @Test
        @DisplayName("다른 유저의 유저명으로 수정하면 예외가 발생한다")
        void duplicatedUsername() {
            createUser("other", "other@test.com");
            UserResponse created = createUser("tester", "tester@test.com");
            UpdateUserRequest request = new UpdateUserRequest("other", null, null, null);

            assertThatThrownBy(() -> userService.update(created.userId(), request))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("다른 유저의 이메일로 수정하면 예외가 발생한다")
        void duplicatedEmail() {
            createUser("other", "other@test.com");
            UserResponse created = createUser("tester", "tester@test.com");
            UpdateUserRequest request = new UpdateUserRequest(null, "other@test.com", null, null);

            assertThatThrownBy(() -> userService.update(created.userId(), request))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("새 프로필로 교체하면 기존 프로필은 삭제된다")
        void replaceProfile() {
            UserResponse created = userService.create(
                    new CreateUserRequest("tester", "tester@test.com", "password", profileRequest()));
            UUID oldProfileId = profileIdOf(created.userId());

            userService.update(created.userId(), new UpdateUserRequest(null, null, null, profileRequest()));

            UUID newProfileId = profileIdOf(created.userId());
            assertThat(newProfileId).isNotEqualTo(oldProfileId);
            assertThat(binaryContentRepository.existsById(newProfileId)).isTrue();
            assertThat(binaryContentRepository.existsById(oldProfileId)).isFalse();
        }

        @Test
        @DisplayName("프로필을 보내지 않으면 기존 프로필이 유지된다")
        void keepProfile() {
            UserResponse created = userService.create(
                    new CreateUserRequest("tester", "tester@test.com", "password", profileRequest()));
            UUID oldProfileId = profileIdOf(created.userId());

            userService.update(created.userId(), new UpdateUserRequest("newName", null, null, null));

            assertThat(profileIdOf(created.userId())).isEqualTo(oldProfileId);
            assertThat(binaryContentRepository.existsById(oldProfileId)).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            UpdateUserRequest request = new UpdateUserRequest("newName", null, null, null);

            assertThatThrownBy(() -> userService.update(UUID.randomUUID(), request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("유저를 삭제하면 UserStatus, 프로필, ReadStatus도 함께 삭제된다")
        void success() {
            UserResponse created = userService.create(
                    new CreateUserRequest("tester", "tester@test.com", "password", profileRequest()));
            UUID profileId = profileIdOf(created.userId());
            ReadStatus readStatus = readStatusRepository.save(new ReadStatus(created.userId(), UUID.randomUUID()));

            userService.delete(created.userId());

            assertThat(userRepository.existsById(created.userId())).isFalse();
            assertThat(userStatusRepository.findByUserId(created.userId())).isEmpty();
            assertThat(binaryContentRepository.existsById(profileId)).isFalse();
            assertThat(readStatusRepository.existsById(readStatus.getId())).isFalse();
        }

        @Test
        @DisplayName("다른 유저의 데이터는 삭제하지 않는다")
        void keepOthers() {
            UserResponse target = createUser("target", "target@test.com");
            UserResponse other = createUser("other", "other@test.com");
            ReadStatus otherReadStatus = readStatusRepository.save(new ReadStatus(other.userId(), UUID.randomUUID()));

            userService.delete(target.userId());

            assertThat(userRepository.existsById(other.userId())).isTrue();
            assertThat(userStatusRepository.findByUserId(other.userId())).isPresent();
            assertThat(readStatusRepository.existsById(otherReadStatus.getId())).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> userService.delete(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    @DisplayName("유저가 저장소에 직접 저장돼 UserStatus가 없으면 조회 시 예외가 발생한다")
    void findWithoutUserStatus() {
        User user = userRepository.save(new User("tester", "tester@test.com", "password"));

        assertThatThrownBy(() -> userService.find(user.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}
