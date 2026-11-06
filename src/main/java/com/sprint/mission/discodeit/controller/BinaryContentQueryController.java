package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BinaryContentQueryController {

    private final BinaryContentService binaryContentService;

    // 쿼리 파라미터로 파일 ID를 받아 바이너리 콘텐츠를 조회합니다.
    @RequestMapping(value = "/api/binaryContent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(
            @RequestParam("binaryContentId") UUID binaryContentId
    ) {
        // 기존 서비스에서 ID에 해당하는 파일 정보를 가져옵니다.
        BinaryContent content = binaryContentService.findById(binaryContentId);

        // 200 OK와 함께 파일 정보를 JSON으로 반환합니다.
        return ResponseEntity.ok(content);
    }
}