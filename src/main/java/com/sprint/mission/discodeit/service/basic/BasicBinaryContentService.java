package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("바이너리 콘텐츠 생성 요청은 null일 수 없습니다.");
        }
        if (request.fileName() == null || request.contentType() == null || request.data() == null) {
            throw new IllegalArgumentException("파일 이름, 콘텐츠 타입, 데이터는 필수입니다.");
        }

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(), request.contentType(), request.data()
        );
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent findById(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "바이너리 콘텐츠를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("ID 목록은 null일 수 없습니다.");
        }
        if (ids.isEmpty()) {
            return List.of();
        }

        Set<UUID> requestedIds = ids.stream()
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        return binaryContentRepository.findAll().stream()
                .filter(binaryContent -> requestedIds.contains(binaryContent.getId()))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        if (!binaryContentRepository.deleteById(id)) {
            throw new IllegalArgumentException("바이너리 콘텐츠를 찾을 수 없습니다: " + id);
        }
    }
}
