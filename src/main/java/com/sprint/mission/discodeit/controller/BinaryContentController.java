package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/binary-contents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{binaryContentId}")
    public BinaryContent getBinaryContent(@PathVariable("binaryContentId") UUID binaryContentId) {
        return binaryContentService.findById(binaryContentId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> getAllBinaryContents(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }


}
