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
    public BinaryContentResponseDto createReadStatus(BinaryContentCreateRequestDto dto) {
        BinaryContent binaryContent = new BinaryContent(dto.getData());

        BinaryContent saveBinaryContent = binaryContentRepository.save(binaryContent);

        return toResponseDto(saveBinaryContent);
    }

    @Override
    public BinaryContentResponseDto findReadStatus(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("바이너리 콘텐츠를 찾을 수 없습니다."));

        return toResponseDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllReadStatusesByUserId(
            List<UUID> binaryContentIds
    ) {
        return binaryContentRepository.findAll()
                .stream()
                .filter(bc ->
                        binaryContentIds.contains(bc.getId()))
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public void deleteReadStatus(UUID binaryContentId) {
        binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("바이너리 콘텐츠를 찾을 수 없습니다."));

        binaryContentRepository.deleteById(binaryContentId);
    }

    private BinaryContentResponseDto toResponseDto(BinaryContent binaryContent) {
        return BinaryContentResponseDto.builder()
                .id(binaryContent.getId())
                .createdAt(binaryContent.getCreatedAt())
                .data(binaryContent.getData())
                .build();
    }
}
