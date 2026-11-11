package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;

import com.sprint.mission.discodeit.entity.BinaryContent;

import com.sprint.mission.discodeit.exception.ResourceNotFoundException;

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

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );

        BinaryContent savedContent =
                binaryContentRepository.save(binaryContent);

        return toResponse(savedContent);
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        BinaryContent binaryContent = requireContent(id);

        return toResponse(binaryContent);
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
    public BinaryContent findEntityById(UUID id) {
        return requireContent(id);
    }

    @Override
    public byte[] readBytes(UUID id) {
        BinaryContent binaryContent = requireContent(id);

        return binaryContent.getBytes();
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

        BinaryContent binaryContent =
                binaryContentRepository.findById(id);

        if (binaryContent == null) {
            throw new ResourceNotFoundException(
                    "파일을 찾을 수 없습니다. ID: " + id
            );
        }

        return binaryContent;
    }

    private BinaryContentResponse toResponse(
            BinaryContent binaryContent
    ) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getSize(),
                binaryContent.getCreatedAt()
        );
    }
}