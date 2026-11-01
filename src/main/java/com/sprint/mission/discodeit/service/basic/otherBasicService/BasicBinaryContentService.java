package com.sprint.mission.discodeit.service.basic.otherBasicService;

import com.sprint.mission.discodeit.dto.request.otherRequest.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.otherService.BinaryContentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BasicBinaryContentService(
            BinaryContentRepository binaryContentRepository
    ) {
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.size()
        );

        binaryContentRepository.save(binaryContent);

        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("바이너리 콘텐츠를 찾을 수 없습니다."));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids);
    }

    @Override
    public void delete(UUID id) {
        find(id);
        binaryContentRepository.deleteById(id);
    }
}