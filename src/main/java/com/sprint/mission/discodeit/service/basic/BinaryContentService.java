package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;


    public BinaryContentResponseDto create(BinaryContentRequestDto binaryContentRequestDto) {
        BinaryContent binaryContent = new BinaryContent(binaryContentRequestDto.fileName(), binaryContentRequestDto.data(), binaryContentRequestDto.contentType());
        binaryContentRepository.save(binaryContent);

        return binaryContentMapper.toResponseDto(binaryContent);
    }

    public BinaryContentResponseDto find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent can not found"));

        return binaryContentMapper.toResponseDto(binaryContent);
    }

    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentIds.stream()
                .map(id -> binaryContentRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("BinaryContent can not found")))
                .map(binaryContentMapper::toResponseDto)
                .toList();
    }

    public void delete(UUID binaryContentId) {
        binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new NoSuchElementException("BinaryContent can not found"));
        binaryContentRepository.deleteById(binaryContentId);
    }

}
