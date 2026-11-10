package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sprint.mission.discodeit.dto.response.BinaryContentResponse.*;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentRequest request) {
        if (request.bytes() == null || request.contentType() == null || request.filename() == null || request.size() == null) {
            throw new IllegalArgumentException("적절하지 않은 인자입니다.");
        }
        BinaryContent binaryContent = new BinaryContent(request.filename(), request.size(), request.contentType(), request.bytes());
        binaryContentRepository.save(binaryContent);
        return from(binaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID contentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(contentId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 content가 없습니다."));

        return from(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> contentIds) {
        return contentIds.stream()
                .map(this::find)
                .toList();
    }

    @Override
    public void delete(UUID contentId) {
        if (!binaryContentRepository.existsById(contentId)) {
            throw new NoSuchElementException("해당 id의 content가 없습니다.");
        }
        binaryContentRepository.deleteById(contentId);
    }
}
