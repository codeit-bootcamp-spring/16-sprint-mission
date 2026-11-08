package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentFindAllByIdInRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        // 이미 다른 서비스에서 이 메서드를 호출할 주체가 유효하다고 검증 되었다고 가정하기 때문에,
        // userId등을 받지 않습니다.
        // 또한 이 서비스는 검증을 담당하지않고 파일의 생성과 삭제만을 담당하기 때문에
        // 메서드의 호출 주체(사용자 등)에 대한 검증 로직은 불필요합니다.

        // 또한 BinaryContent는 BinaryContentRepository의 data를 뒤져서 중복되는 객체가 있는 지 여부를
        // 검사 할 필요가 없습니다. 모든 BinaryContent 객체는 고유한 ID를 가지고, 같은 내용의 객체를 중복으로
        // 생성할 수 있기 때문입니다.

        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
        this.binaryContentRepository.save(binaryContent);

        return this.toBinaryContentResponse(binaryContent);
    }

    private BinaryContentResponse toBinaryContentResponse(@NonNull BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getContentType()
        );
    }

    @Override
    public BinaryContentResponse find(UUID binaryContentId) {
        BinaryContent binaryContent = this.binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException(
                        "ID가 " + binaryContentId + "인 파일을 찾을 수 없습니다."));

        return this.toBinaryContentResponse(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(BinaryContentFindAllByIdInRequest request) {
        // List<BinaryContent> binaryContentList = this.binaryContentRepository.findAll();
        List<BinaryContentResponse> binaryContentResponses = new ArrayList<>();

        /*
        for (BinaryContent binaryContent : binaryContentList) {
            for (UUID binaryContentId : request.binaryContentIds()) {
                if (binaryContent.getId().equals(binaryContentId)) {
                    BinaryContentResponse binaryContentResponse = this.toBinaryContentResponse(binaryContent);
                    binaryContentResponses.add(binaryContentResponse);
                }
            }
        }
         */

        // 이 구조는 BinaryContentFindAllByIdInRequest DTO의 binaryContentId 목록 중 binaryContentId가 하나라도 존재하지 않으면
        // 그 binaryContentId에 대해서 예외를 발생 시키고 반환값을 반환하지 않습니다.
        // 따라서 이전에 조회한 객체들이 있더라도 결과 리스트는 반환되지 않습니다.
        for (UUID binaryContentId : request.binaryContentIds()) {
            BinaryContent binaryContent = this.binaryContentRepository.findById(binaryContentId)
                    .orElseThrow(() -> new NoSuchElementException(
                            "ID가 " + binaryContentId + "인 파일을 찾을 수 없습니다."));
            BinaryContentResponse binaryContentResponse = this.toBinaryContentResponse(binaryContent);
            binaryContentResponses.add(binaryContentResponse);
        }

        return binaryContentResponses;
    }

    @Override
    public void delete(UUID id) {
        // this.binaryContentRepository.deleteById(id)

        // 삭제 대상이 존재하지 않을 때 예외를 발생시키는 비즈니스 규칙은 Service에서 처리하는것이 좋습니다.
        if (!this.binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("ID가 " + id + "인 파일을 찾을 수 없습니다.");
        }

        this.binaryContentRepository.deleteById(id);
    }
}
