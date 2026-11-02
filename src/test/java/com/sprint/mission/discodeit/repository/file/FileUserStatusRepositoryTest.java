package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

// 실제 file-data-map 폴더에 저장하므로, 테스트가 만든 파일은 끝날 때 지운다
class FileUserStatusRepositoryTest {
    @TempDir
    Path tempDir;
    private FileUserStatusRepository repository;
    private final List<UUID> savedIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        repository = new FileUserStatusRepository(tempDir.toString());
    }

    @AfterEach
    void tearDown() {
        savedIds.forEach(repository::deleteById);
    }

    private UserStatus saveUserStatus(UUID userId) {
        UserStatus userStatus = repository.save(new UserStatus(userId));
        savedIds.add(userStatus.getId());
        return userStatus;
    }

    @Test
    @DisplayName("저장한 UserStatus를 id로 조회할 수 있다")
    void saveAndFindById() {
        UserStatus saved = saveUserStatus(UUID.randomUUID());

        Optional<UserStatus> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(saved.getUserId());
        assertThat(found.get().getLastActiveAt()).isEqualTo(saved.getLastActiveAt());
    }

    @Test
    @DisplayName("존재하지 않는 id로 조회하면 빈 Optional을 반환한다")
    void findByIdNotFound() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("userId로 UserStatus를 조회할 수 있다")
    void findByUserId() {
        UUID userId = UUID.randomUUID();
        UserStatus saved = saveUserStatus(userId);
        saveUserStatus(UUID.randomUUID());

        Optional<UserStatus> found = repository.findByUserId(userId);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("존재하지 않는 userId로 조회하면 빈 Optional을 반환한다")
    void findByUserIdNotFound() {
        assertThat(repository.findByUserId(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("findAll은 저장한 UserStatus를 모두 포함한다")
    void findAll() {
        UserStatus first = saveUserStatus(UUID.randomUUID());
        UserStatus second = saveUserStatus(UUID.randomUUID());

        assertThat(repository.findAll()).extracting(UserStatus::getId)
                .contains(first.getId(), second.getId());
    }

    @Test
    @DisplayName("existsById는 저장 여부를 반환한다")
    void existsById() {
        UserStatus saved = saveUserStatus(UUID.randomUUID());

        assertThat(repository.existsById(saved.getId())).isTrue();
        assertThat(repository.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById로 삭제하면 더 이상 조회되지 않는다")
    void deleteById() {
        UserStatus saved = saveUserStatus(UUID.randomUUID());

        repository.deleteById(saved.getId());

        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 id를 삭제해도 예외가 발생하지 않는다")
    void deleteByIdNotFound() {
        assertThatCode(() -> repository.deleteById(UUID.randomUUID()))
                .doesNotThrowAnyException();
    }
}
