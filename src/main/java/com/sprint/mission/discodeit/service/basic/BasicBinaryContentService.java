package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponseDto create(BinaryContentCreateRequestDto dto) {
        BinaryContent binaryContent = new BinaryContent(dto.getContentType(), dto.getBytes());

        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        return toResponseDto(savedBinaryContent);
    }

    @Override
    public BinaryContent find(UUID binaryContentId) {
//        BinaryContent binaryContent =
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("바이너리 콘텐츠를 찾을 수 없습니다."));

//        return toResponseDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(
            List<UUID> binaryContentIds
    ) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("바이너리 콘텐츠를 찾을 수 없습니다."));

        binaryContentRepository.deleteById(binaryContentId);
    }

    private BinaryContentResponseDto toResponseDto(BinaryContent binaryContent) {
        return BinaryContentResponseDto.builder()
                .id(binaryContent.getId())
                .createdAt(binaryContent.getCreatedAt())
                .contentType(binaryContent.getContentType())
                .bytes(binaryContent.getBytes())
                .build();
    }
}
