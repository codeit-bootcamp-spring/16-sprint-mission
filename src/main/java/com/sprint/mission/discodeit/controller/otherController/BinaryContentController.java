package com.sprint.mission.discodeit.controller.otherController;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.service.otherService.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-content")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 바이너리 콘텐츠 생성(POST)
    @RequestMapping(method = RequestMethod.POST)
    public BinaryContent postBinaryContent(@RequestBody BinaryContentCreateRequest request) {
        return binaryContentService.create(request);
    }

    // 바이너리 콘텐츠 단건 조회(GET)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BinaryContent getBinaryContent(@PathVariable UUID id) {
        return binaryContentService.find(id);
    }

    // 바이너리 콘텐츠 다건 조회(GET)
    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> getAllBinaryContent(@RequestParam List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }
}
