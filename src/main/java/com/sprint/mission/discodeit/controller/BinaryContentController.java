package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.basic.BinaryContentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/binaryContents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<List<BinaryContentResponse>> findAllFilesByIds(@RequestParam(name = "binaryContentIds") List<@NotNull UUID> binaryContentIds) {
        return ResponseEntity.status(HttpStatus.OK).body(this.binaryContentService.findAllByIdIn(binaryContentIds));
    }

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> findById(@NotNull @PathVariable UUID binaryContentId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.binaryContentService.findById(binaryContentId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> findAllFiles() {
        return ResponseEntity.status(HttpStatus.OK).body(this.binaryContentService.findAll());
    }
}
