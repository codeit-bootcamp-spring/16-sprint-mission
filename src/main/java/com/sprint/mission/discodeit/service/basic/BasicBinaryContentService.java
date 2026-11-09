package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService
        implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(
            CreateBinaryContentRequest request
    ) {
        if (request == null
                || request.fileName() == null
                || request.fileName().isBlank()
                || request.contentType() == null
                || request.contentType().isBlank()
                || request.bytes() == null) {
            throw new IllegalArgumentException(
                    "파일 정보가 올바르지 않습니다."
            );
        }

        BinaryContent content = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );

        BinaryContent savedContent =
                binaryContentRepository.save(content);

        return toResponse(savedContent);
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        return toResponse(requireContent(id));
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(
            Collection<UUID> ids
    ) {
        if (ids == null) {
            throw new IllegalArgumentException(
                    "파일 ID 목록이 없습니다."
            );
        }

        return binaryContentRepository.findAllByIdIn(ids)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public byte[] readBytes(UUID id) {
        BinaryContent content = requireContent(id);

        return content.getBytes().clone();
    }

    @Override
    public void delete(UUID id) {
        requireContent(id);

        binaryContentRepository.delete(id);
    }

    private BinaryContent requireContent(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "파일 ID가 없습니다."
            );
        }

        BinaryContent content =
                binaryContentRepository.findById(id);

        if (content == null) {
            throw new IllegalArgumentException(
                    "파일을 찾을 수 없습니다."
            );
        }

        return content;
    }

    private BinaryContentResponse toResponse(
            BinaryContent content
    ) {
        return new BinaryContentResponse(
                content.getId(),
                content.getFileName(),
                content.getContentType(),
                content.getSize(),
                content.getCreatedAt()
        );
    }
}