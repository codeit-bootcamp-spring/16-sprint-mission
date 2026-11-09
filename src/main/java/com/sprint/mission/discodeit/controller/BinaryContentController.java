package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.basic.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("/{contentId}")
    public ResponseEntity<BinaryContentResponseDto> findBinaryContent(@PathVariable UUID contentId) {
        BinaryContentResponseDto binaryContentResponseDto = binaryContentService.find(contentId);
        return ResponseEntity.ok(binaryContentResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentResponseDto>> findAll(@RequestParam List<UUID> contentIds) {
        List<BinaryContentResponseDto> binaryContentResponseDtoList = binaryContentService.findAllByIdIn(contentIds);
        return ResponseEntity.ok(binaryContentResponseDtoList);
    }

}
