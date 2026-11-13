package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exceptions.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.size(),
                request.bytes()
        );

        // 생성한 바이너리 콘텐츠를 Repository에 저장한다.
        binaryContentRepository.save(binaryContent);

        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("바이너리 콘텐츠를 찾을 수 없습니다."));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> binaryContents =
                binaryContentRepository.findAllByIdIn(ids);

        // 요청한 UUID 중 조회되지 않은 바이너리 콘텐츠가 있는지 확인한다.
        Set<UUID> foundIds = binaryContents.stream()
                .map(BinaryContent::getId)
                .collect(Collectors.toSet());

        if (!foundIds.containsAll(ids)) {
            throw new ResourceNotFoundException(
                    "일부 바이너리 콘텐츠를 찾을 수 없습니다."
            );
        }

        return binaryContents;
        // return binaryContentRepository.findAllByIdIn(ids);
    }

    @Override
    public void delete(UUID id) {
        find(id);
        binaryContentRepository.deleteById(id);
    }
}