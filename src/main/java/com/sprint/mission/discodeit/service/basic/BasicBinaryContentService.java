package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateDto dto) {
        BinaryContent binaryContent = new BinaryContent(
                dto.fileName(),
                dto.contentType(),
                dto.fileSize(),
                dto.fileUrl()
        );
        return binaryContentRepository.save(binaryContent);
    }
    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
    }
    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<BinaryContent> contents = binaryContentRepository.findByIdIn(ids);

        return contents.stream()
                .map(binaryContent -> BinaryContentResponseDto.from(binaryContent))
                .toList();
    }
    @Override
    public void delete(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));

        binaryContentRepository.deleteById(id);
    }
}
