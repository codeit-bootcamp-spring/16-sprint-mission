
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ApiSpecDto;
import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(
        name = "ReadStatus",
        description = "Message 읽음 상태 API"
)
public class ReadStatusController {

    private final ReadStatusService readStatusService;
    private final ReadStatusRepository readStatusRepository;

    @GetMapping
    @Operation(summary = "사용자별 읽음 상태 목록 조회")
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "사용자 ID가 필요합니다."
            );
        }

        List<ReadStatus> readStatuses =
                readStatusRepository.findAllByUserId(userId);

        return ResponseEntity.ok(readStatuses);
    }

    @PostMapping
    @Operation(summary = "읽음 상태 생성")
    public ResponseEntity<ReadStatus> create(
            @RequestBody ApiSpecDto.ReadStatusCreate request
    ) {
        if (request == null ||
                request.userId() == null ||
                request.channelId() == null) {

            throw new IllegalArgumentException(
                    "사용자 ID와 채널 ID가 필요합니다."
            );
        }

        readStatusService.create(
                new CreateReadStatusRequest(
                        request.userId(),
                        request.channelId(),
                        request.lastReadAt()
                )
        );

        ReadStatus created =
                readStatusRepository.findByUserIdAndChannelId(
                        request.userId(),
                        request.channelId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PatchMapping("/{readStatusId}")
    @Operation(summary = "읽음 상태 수정")
    public ResponseEntity<ReadStatus> update(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ApiSpecDto.ReadStatusUpdate request
    ) {
        if (request == null || request.newLastReadAt() == null) {
            throw new IllegalArgumentException(
                    "수정할 읽음 시간이 필요합니다."
            );
        }

        readStatusService.update(
                new UpdateReadStatusRequest(
                        readStatusId,
                        request.newLastReadAt()
                )
        );

        ReadStatus updated =
                readStatusRepository.findById(readStatusId);

        return ResponseEntity.ok(updated);
    }
}
