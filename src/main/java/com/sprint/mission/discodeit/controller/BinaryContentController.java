package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.annotation.swagger.binarycontent.BinaryContentListDocs;
import com.sprint.mission.discodeit.annotation.swagger.binarycontent.BinaryContentReadDocs;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @BinaryContentReadDocs
    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(
            @PathVariable UUID binaryContentId) {

        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    @BinaryContentListDocs
    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }
}