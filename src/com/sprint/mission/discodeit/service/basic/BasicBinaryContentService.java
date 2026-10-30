package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    // 첨부파일 생성
    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("첨부파일 생성 정보는 필수입니다.");
        }

        if (request.fileName() == null || request.fileName().isBlank()) {
            throw new IllegalArgumentException("파일 이름은 필수입니다.");
        }

        if (request.contentType() == null || request.contentType().isBlank()) {
            throw new IllegalArgumentException("파일 형식은 필수입니다.");
        }

        if (request.size() < 0) {
            throw new IllegalArgumentException("파일 크기는 0 이상이어야 합니다.");
        }

        Objects.requireNonNull(request.data(), "파일 데이터는 필수입니다.");

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.size(),
                request.data()
        );

        return binaryContentRepository.save(binaryContent);
    }

    // ID로 첨부파일 조회
    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Objects.requireNonNull(id, "첨부파일 ID는 필수입니다.");

        return binaryContentRepository.findById(id);
    }

    // 여러 ID로 첨부파일 조회
    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        Objects.requireNonNull(ids, "첨부파일 ID 목록은 필수입니다.");

        if (ids.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("첨부파일 ID는 null일 수 없습니다.");
        }

        return binaryContentRepository.findAllByIdIn(ids);
    }

    // ID로 첨부파일 삭제
    @Override
    public void deleteById(UUID id) {
        Objects.requireNonNull(id, "첨부파일 ID는 필수입니다.");

        binaryContentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("첨부파일 정보를 찾을 수 없습니다.")
                );

        binaryContentRepository.deleteById(id);
    }
}