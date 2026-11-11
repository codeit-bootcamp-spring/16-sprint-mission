package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicBinaryContentServiceTest {
    private BinaryContentRepository binaryContentRepository;
    private BasicBinaryContentService binaryContentService;

    @BeforeEach
    void setUp() {
        binaryContentRepository = new JCFBinaryContentRepository();
        binaryContentService = new BasicBinaryContentService(binaryContentRepository);
    }

    private BinaryContentRequest validRequest() {
        byte[] bytes = {1, 2, 3};
        return new BinaryContentRequest("profile.png", (long) bytes.length, "image/png", bytes);
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("BinaryContent를 생성하고 저장한다")
        void success() {
            BinaryContentRequest request = validRequest();

            BinaryContentResponse response = binaryContentService.create(request);

            assertThat(response.filename()).isEqualTo(request.filename());
            assertThat(response.size()).isEqualTo(request.size());
            assertThat(response.contentType()).isEqualTo(request.contentType());
            assertThat(response.bytes()).isEqualTo(request.bytes());
            assertThat(binaryContentRepository.existsById(response.contentId())).isTrue();
        }

        @Test
        @DisplayName("필드 중 하나라도 null이면 예외가 발생한다")
        void nullField() {
            BinaryContentRequest request = new BinaryContentRequest("profile.png", 3L, null, new byte[]{1, 2, 3});

            assertThatThrownBy(() -> binaryContentService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("find")
    class Find {
        @Test
        @DisplayName("id로 BinaryContent를 조회한다")
        void success() {
            BinaryContentResponse created = binaryContentService.create(validRequest());

            BinaryContentResponse found = binaryContentService.find(created.contentId());

            assertThat(found.contentId()).isEqualTo(created.contentId());
            assertThat(found.bytes()).isEqualTo(created.bytes());
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> binaryContentService.find(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("findAllByIdIn")
    class FindAllByIdIn {
        @Test
        @DisplayName("id 목록에 해당하는 BinaryContent를 모두 조회한다")
        void success() {
            BinaryContentResponse first = binaryContentService.create(validRequest());
            BinaryContentResponse second = binaryContentService.create(validRequest());
            binaryContentService.create(validRequest());

            List<BinaryContentResponse> result = binaryContentService.findAllByIdIn(List.of(first.contentId(), second.contentId()));

            assertThat(result).extracting(BinaryContentResponse::contentId)
                    .containsExactlyInAnyOrder(first.contentId(), second.contentId());
        }

        @Test
        @DisplayName("목록에 존재하지 않는 id가 하나라도 있으면 예외가 발생한다")
        void containsNotFoundId() {
            BinaryContentResponse created = binaryContentService.create(validRequest());
            List<UUID> ids = List.of(created.contentId(), UUID.randomUUID());

            assertThatThrownBy(() -> binaryContentService.findAllByIdIn(ids))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("빈 목록이면 빈 리스트를 반환한다")
        void emptyIds() {
            assertThat(binaryContentService.findAllByIdIn(List.of())).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("id로 BinaryContent를 삭제한다")
        void success() {
            BinaryContentResponse created = binaryContentService.create(validRequest());

            binaryContentService.delete(created.contentId());

            assertThat(binaryContentRepository.existsById(created.contentId())).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> binaryContentService.delete(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
