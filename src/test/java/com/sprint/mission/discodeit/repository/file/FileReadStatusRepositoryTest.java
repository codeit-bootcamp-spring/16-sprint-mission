package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

// 테스트마다 JUnit이 만들어 주는 임시 폴더에 저장하므로, 실제 저장 폴더를 건드리지 않는다
class FileReadStatusRepositoryTest {
    @TempDir
    Path tempDir;

    private FileReadStatusRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FileReadStatusRepository(tempDir.toString());
    }

    private ReadStatus saveReadStatus() {
        return repository.save(new ReadStatus(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("저장한 ReadStatus를 id로 조회할 수 있다")
    void saveAndFindById() {
        ReadStatus saved = saveReadStatus();

        Optional<ReadStatus> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(saved.getUserId());
        assertThat(found.get().getChannelId()).isEqualTo(saved.getChannelId());
    }

    @Test
    @DisplayName("수정 후 다시 저장하면 수정된 값이 조회된다")
    void saveAfterUpdate() {
        ReadStatus saved = saveReadStatus();
        Instant newLastReadAt = Instant.now().minusSeconds(600);

        saved.update(newLastReadAt);
        repository.save(saved);

        assertThat(repository.findById(saved.getId()))
                .get()
                .extracting(ReadStatus::getLastReadAt)
                .isEqualTo(newLastReadAt);
    }

    @Test
    @DisplayName("존재하지 않는 id로 조회하면 빈 Optional을 반환한다")
    void findByIdNotFound() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("findAll은 저장한 ReadStatus를 모두 포함한다")
    void findAll() {
        ReadStatus first = saveReadStatus();
        ReadStatus second = saveReadStatus();

        assertThat(repository.findAll()).extracting(ReadStatus::getId)
                .contains(first.getId(), second.getId());
    }

    @Test
    @DisplayName("existsById는 저장 여부를 반환한다")
    void existsById() {
        ReadStatus saved = saveReadStatus();

        assertThat(repository.existsById(saved.getId())).isTrue();
        assertThat(repository.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById로 삭제하면 더 이상 조회되지 않는다")
    void deleteById() {
        ReadStatus saved = saveReadStatus();

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
