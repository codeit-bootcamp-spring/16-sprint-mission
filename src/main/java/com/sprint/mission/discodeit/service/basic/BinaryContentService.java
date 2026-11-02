package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentCreateResponse create(BinaryContentCreateRequest createRequest) {
        String fileName = createRequest.fileName();
        String extension = createRequest.extension();
        byte[] content = createRequest.content();

        BinaryContent binaryContent = new BinaryContent(fileName, extension, content);
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

    public List<BinaryContentFindResponse> findAllByIdIn(List<UUID> ids) {
        List<UUID> distinctIds = new ArrayList<>(new LinkedHashSet<>(ids));

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

        this.binaryContentRepository.delete(id);
    }
}
