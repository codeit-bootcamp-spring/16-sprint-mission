package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDownloadDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    // 기존 DTO 방식 첨부파일 생성
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
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

        if (request.data() == null) {
            throw new IllegalArgumentException("파일 데이터는 필수입니다.");
        }

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.size(),
                request.data()
        );

        BinaryContent savedContent = binaryContentRepository.save(binaryContent);
        return toDto(savedContent);
    }

    // MultipartFile 방식 첨부파일 생성
    @Override
    public BinaryContentDto create(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("첨부파일은 필수입니다.");
        }

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일 이름은 필수입니다.");
        }

        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        try {
            BinaryContent binaryContent = new BinaryContent(
                    fileName,
                    contentType,
                    file.getSize(),
                    file.getBytes()
            );

            BinaryContent savedContent =
                    binaryContentRepository.save(binaryContent);

            return toDto(savedContent);

        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.", e);
        }
    }

    // ID로 첨부파일 조회
    @Override
    public Optional<BinaryContentDto> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        return binaryContentRepository.findById(id)
                .map(this::toDto);
    }

    // 원본 BinaryContent 엔티티 조회
    @Override
    public Optional<BinaryContent> findEntityById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        return binaryContentRepository.findById(id);
    }

    // 여러 ID로 첨부파일 조회
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("첨부파일 ID 목록은 필수입니다.");
        }

        if (ids.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("첨부파일 ID는 null일 수 없습니다.");
        }

        return binaryContentRepository.findAllByIdIn(ids)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 파일 하나의 실제 데이터 조회
    @Override
    public Optional<BinaryContentDownloadDto> downloadById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        return binaryContentRepository.findById(id)
                .map(this::toDownloadDto);
    }

    // 여러 파일의 실제 데이터 조회
    @Override
    public List<BinaryContentDownloadDto> downloadAllByIdIn(List<UUID> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("첨부파일 ID 목록은 필수입니다.");
        }

        if (ids.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("첨부파일 ID는 null일 수 없습니다.");
        }

        return binaryContentRepository.findAllByIdIn(ids)
                .stream()
                .map(this::toDownloadDto)
                .toList();
    }

    // ID로 첨부파일 삭제
    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        binaryContentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("첨부파일 정보를 찾을 수 없습니다.")
                );

        binaryContentRepository.deleteById(id);
    }

    // 일반 조회 DTO 변환
    private BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getSize()
        );
    }

    // 다운로드 DTO 변환
    private BinaryContentDownloadDto toDownloadDto(BinaryContent binaryContent) {
        return new BinaryContentDownloadDto(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getData()
        );
    }
}