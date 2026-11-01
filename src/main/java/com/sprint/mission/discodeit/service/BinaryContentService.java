package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentService(BinaryContentRepository binaryContentRepository) {
        this.binaryContentRepository = binaryContentRepository;
    }

    public BinaryContentResponseDto toResponseDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(binaryContent.getId(), binaryContent.getFileName(), binaryContent.getData(), binaryContent.getContentType());
    }

    public BinaryContentResponseDto create(BinaryContentRequestDto binaryContentRequestDto) {
        BinaryContent binaryContent = new BinaryContent(binaryContentRequestDto.fileName(), binaryContentRequestDto.data(), binaryContentRequestDto.contentType());
        binaryContentRepository.save(binaryContent);

        return this.toResponseDto(binaryContent);
    }

    public BinaryContentResponseDto find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent can not found"));

        return this.toResponseDto(binaryContent);
    }

    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentIds.stream()
                .map(id -> binaryContentRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("BinaryContent can not found")))
                .map(this::toResponseDto)
                .toList();
    }

    public void delete(UUID binaryContentId) {
        binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new NoSuchElementException("BinaryContent can not found"));
        binaryContentRepository.deleteById(binaryContentId);
    }

}
