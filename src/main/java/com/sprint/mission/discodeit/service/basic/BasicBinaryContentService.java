package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final CommonValidator validator;

    @Override
    public BinaryContent findById(UUID id) {
        validator.validateNotNull(id, "파일 ID는 null일 수 없습니다.");

        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "파일이 존재하지 않습니다. ID: " + id
                ));
    }

    @Override
    public List<BinaryContent> findAllByIds(List<UUID> ids) {
        validator.validateNotNull(ids, "파일 ID 목록은 null일 수 없습니다.");

        return ids.stream()
                .map(this::findById)
                .toList();
    }
}