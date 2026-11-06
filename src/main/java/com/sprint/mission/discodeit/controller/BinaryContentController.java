package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentFindAllByIdInRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentFindResponse;
import com.sprint.mission.discodeit.service.basic.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/files")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<List<BinaryContentFindResponse>> findAllFilesByIds(@RequestBody BinaryContentFindAllByIdInRequest findRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.binaryContentService.findAllByIdIn(findRequest));
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentFindResponse>> findAllFiles() {
        return ResponseEntity.status(HttpStatus.OK).body(this.binaryContentService.findAll());
    }
}
