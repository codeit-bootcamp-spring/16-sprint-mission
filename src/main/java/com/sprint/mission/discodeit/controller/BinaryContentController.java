package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/binary-content")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.GET)
    public BinaryContentResponseDto findById(
            @RequestParam UUID binaryContentId
    ) {
        return binaryContentService.findBinaryContent(binaryContentId);
    }

    @RequestMapping(value = "/multiple", method = RequestMethod.GET)
    public List<BinaryContentResponseDto> findAllByIds(
            @RequestParam List<UUID> binaryContentIds
    ) {
        return binaryContentService.findAllBinaryContentsByIds(binaryContentIds);
    }
}
