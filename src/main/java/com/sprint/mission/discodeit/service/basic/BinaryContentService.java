package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BinaryContentResponse create(MultipartFile createRequest) {
        String fileName = createRequest.getOriginalFilename();
        Long size = createRequest.getSize();
        String contentType = createRequest.getContentType();
        byte[] content = null;

        try {
            content = createRequest.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BinaryContent binaryContent = new BinaryContent(fileName, content, size, contentType);
        this.binaryContentRepository.save(binaryContent);
        return BinaryContentResponse.from(binaryContent);
    }

    public BinaryContentResponse findById(UUID id) {
        BinaryContent binaryContent = this.binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("바이너리 데이터가 존재하지 않습니다."));
        return BinaryContentResponse.from(binaryContent);
    }

    public List<BinaryContentResponse> findAll() {
        return this.binaryContentRepository.findAll()
                .stream()
                .map(BinaryContentResponse::from)
                .toList();
    }

    public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
        List<UUID> distinctIds = new ArrayList<>(new LinkedHashSet<>(binaryContentIds));

        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(distinctIds);
        if (binaryContents.size() != distinctIds.size())
            throw new IllegalStateException("조회 개채들 중 존재하지 않는 바이너리 데이터가 있습니다.");

        return binaryContents
                .stream()
                .map(BinaryContentResponse::from)
                .toList();
    }

    public void delete(UUID id) {
        BinaryContent binaryContent = this.binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 바이너리 데이터입니다."));
        binaryContent.validateNotDeleted();

        this.userRepository.findByProfileId(id).ifPresent(user -> {
            user.assignProfileImage(null);
            this.userRepository.update(user.getId(), user);
        });

        this.messageRepository.findByAttachmentId(id).ifPresent(message -> {
            message.removeAttachment(id);
            this.messageRepository.update(message.getId(), message);
        });

        this.binaryContentRepository.delete(id);
    }
}
