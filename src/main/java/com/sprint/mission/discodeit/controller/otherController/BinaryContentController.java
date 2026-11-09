package com.sprint.mission.discodeit.controller.otherController;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.service.otherService.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 바이너리 콘텐츠 생성(POST)
    @RequestMapping(method = RequestMethod.POST)
    public BinaryContent postBinaryContent(@RequestBody BinaryContentCreateRequest request) {
        return binaryContentService.create(request);
    }

    // 바이너리 콘텐츠 단건 조회(GET)
    // 기존 코드는 UUID를 URL 경로로 전달받아 바이너리 콘텐츠를 반환했다면,
    // 변경 코드는 UUID를 요청 파라미터로 전달받고, ResponseEntity로 HTTP 응답을 직접 제어한다.
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> getBinaryContent(@RequestParam UUID binaryContentId) {
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    // 바이너리 콘텐츠 다건 조회(GET)
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<BinaryContent> getAllBinaryContent(@RequestParam List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }
}
