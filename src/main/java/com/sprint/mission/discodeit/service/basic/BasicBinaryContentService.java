package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // 파일 생성
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {

        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException(
                    "User not found with id " + request.getUserId()
            );
        }

        if (request.getMessageId() != null
                && messageRepository.findById(request.getMessageId()).isEmpty()) {
            throw new NoSuchElementException(
                    "Message not found with id " + request.getMessageId()
            );
        }

        BinaryContent binaryContent = new BinaryContent(
                request.getFileName(),
                request.getContentType(),
                request.getData(),
                request.getUserId(),
                request.getMessageId()
        );

        binaryContentRepository.save(binaryContent);

        return toDto(binaryContent);
    }

    // 파일 단건 조회
    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository
                .findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException(
                        "BinaryContent not found with id " + binaryContentId
                ));

        return toDto(binaryContent);
    }

    // 전체 파일 조회
    @Override
    public List<BinaryContentDto> findAll() {
        return binaryContentRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // 여러 ID로 파일 조회
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(this::toDto)
                .toList();
    }

    // 특정 사용자의 파일 조회
    @Override
    public List<BinaryContentDto> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(
                    "User not found with id " + userId
            );
        }

        return binaryContentRepository.findAll().stream()
                .filter(content -> userId.equals(content.getUserId()))
                .map(this::toDto)
                .toList();
    }

    // 특정 메시지에 연결된 파일 조회
    @Override
    public List<BinaryContentDto> findAllByMessageId(UUID messageId) {
        if (messageRepository.findById(messageId).isEmpty()) {
            throw new NoSuchElementException(
                    "Message not found with id " + messageId
            );
        }

        return binaryContentRepository.findAllByMessageId(messageId).stream()
                .map(this::toDto)
                .toList();
    }

    // 파일 삭제
    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException(
                    "BinaryContent not found with id " + binaryContentId
            );
        }

        binaryContentRepository.deleteById(binaryContentId);
    }

    // Entity를 DTO로 변환
    private BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getUserId(),
                binaryContent.getMessageId()
        );
    }
}