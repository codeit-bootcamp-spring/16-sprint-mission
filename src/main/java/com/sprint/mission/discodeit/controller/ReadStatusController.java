package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/read-status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatusResponseDto create(
            @RequestBody ReadStatusCreateRequestDto request
    ) {
        return  readStatusService.createReadStatus(request);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ReadStatusResponseDto update(
            @RequestHeader("READ-STATUS-UUID") UUID readStatusId
    ) {
        return readStatusService.updateReadStatus(readStatusId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatusResponseDto> findAllUserReadStatuses(
            @RequestHeader("USER-UUID") UUID userId
    ) {
        return readStatusService.findAllReadStatusesByUserId(userId);
    }
}
