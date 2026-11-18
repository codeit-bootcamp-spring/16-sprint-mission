package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentFindAllByIdInRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BinaryContentCreateResponse create(BinaryContentCreateRequest createRequest) {
        String fileName = createRequest.fileName();
        byte[] content = createRequest.content();

        BinaryContent binaryContent = new BinaryContent(fileName, content);
        this.binaryContentRepository.save(binaryContent);
        return BinaryContentCreateResponse.from(binaryContent);
    }

    public BinaryContentFindResponse findById(UUID id) {
        BinaryContent binaryContent = this.binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("바이너리 데이터가 존재하지 않습니다."));
        return BinaryContentFindResponse.from(binaryContent);
    }

    public List<BinaryContentFindResponse> findAll() {
        return this.binaryContentRepository.findAll()
                .stream()
                .map(BinaryContentFindResponse::from)
                .toList();
    }

    public List<BinaryContentFindResponse> findAllByIdIn(BinaryContentFindAllByIdInRequest findRequest) {
        List<UUID> distinctIds = new ArrayList<>(new LinkedHashSet<>(findRequest.ids()));

        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(distinctIds);
        if (binaryContents.size() != distinctIds.size())
            throw new IllegalStateException("조회 개채들 중 존재하지 않는 바이너리 데이터가 있습니다.");

        return binaryContents
                .stream()
                .map(BinaryContentFindResponse::from)
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
