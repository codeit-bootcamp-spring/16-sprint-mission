package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    //바이너리 파일을 1개 조회 및 다운로드
    @GetMapping("/{fileId}")
    public BinaryContent getFile(@PathVariable UUID fileId) {
        return binaryContentService.find(fileId);
    }
    // 바이너리 파일을 여러 개 조회 (ID 목록 기반 데이터 목록 조회)
    @GetMapping
    public List<BinaryContent> getFilesByIds(@RequestParam List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }
}