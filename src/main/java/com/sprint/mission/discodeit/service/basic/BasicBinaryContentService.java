package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.fileName(),request.contentType(),request.bytes());
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent findById(UUID id) {
        return binaryContentRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("파일을 찾을 수 없음"));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAll();
        return binaryContents.stream().filter(b->ids.contains(b.getId())).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("파일을 찾을 수 없음"));

        binaryContentRepository.deleteById(id);
    }
}