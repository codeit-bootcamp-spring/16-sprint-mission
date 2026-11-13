package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
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
    public BinaryContent find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() ->
                        new BusinessLogicException(
                                ExceptionCode.BINARY_CONTENT_NOT_FOUND,
                                binaryContentId
                        ));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(
            List<UUID> binaryContentIds
    ) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds);
    }
}
