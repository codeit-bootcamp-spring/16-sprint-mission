package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ApiSpecDto;
import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;

import com.sprint.mission.discodeit.entity.ReadStatus;

import com.sprint.mission.discodeit.exception.ResourceNotFoundException;

import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import com.sprint.mission.discodeit.service.ReadStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

    private final ReadStatusService readStatusService;
    private final ReadStatusRepository readStatusRepository;

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "userId는 필수입니다."
            );
        }

        return ResponseEntity.ok(
                readStatusRepository.findAllByUserId(userId)
        );
    }

    @Operation(summary = "Message 읽음 상태 생성")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(
            @RequestBody ApiSpecDto.ReadStatusCreate request
    ) {
        if (request == null
                || request.userId() == null
                || request.channelId() == null) {
            throw new IllegalArgumentException(
                    "userId와 channelId는 필수입니다."
            );
        }

        if (readStatusRepository.findByUserIdAndChannelId(
                request.userId(),
                request.channelId()
        ) != null) {
            throw new IllegalArgumentException(
                    "이미 읽음 상태가 존재합니다."
            );
        }

        try {
            readStatusService.create(
                    new CreateReadStatusRequest(
                            request.userId(),
                            request.channelId(),
                            request.lastReadAt()
                    )
            );
        } catch (IllegalStateException exception) {
            throw new IllegalArgumentException(
                    exception.getMessage(),
                    exception
            );
        }

        ReadStatus created =
                readStatusRepository.findByUserIdAndChannelId(
                        request.userId(),
                        request.channelId()
                );

        if (created == null) {
            throw new IllegalStateException(
                    "읽음 상태 저장 결과를 확인할 수 없습니다."
            );
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @Operation(summary = "Message 읽음 상태 수정")
    @RequestMapping(
            value = "/{readStatusId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<ReadStatus> update(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ApiSpecDto.ReadStatusUpdate request
    ) {
        if (request == null
                || request.newLastReadAt() == null) {
            throw new IllegalArgumentException(
                    "newLastReadAt은 필수입니다."
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

        if (updated == null) {
            throw new ResourceNotFoundException(
                    "읽음 상태를 찾을 수 없습니다."
            );
        }

        return ResponseEntity.ok(updated);
    }
}