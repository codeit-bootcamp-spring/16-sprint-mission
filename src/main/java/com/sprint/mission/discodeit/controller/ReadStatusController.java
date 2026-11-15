package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.service.ReadStatusService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadStatusResponseDto create(
            @Valid @RequestBody ReadStatusCreateRequestDto request
    ) {
        return readStatusService.createReadStatus(request);
    }

    @PatchMapping("/{readStatusId}")
    public ReadStatusResponseDto update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequestDto request
    ) {
        return readStatusService.updateReadStatus(readStatusId, request);
    }

    @GetMapping
    public List<ReadStatusResponseDto> findAllUserReadStatuses(
            @RequestParam UUID userId
    ) {
        return readStatusService.findAllReadStatusesByUserId(userId);
    }
}
