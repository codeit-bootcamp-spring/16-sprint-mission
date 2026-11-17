
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read-statuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ReadStatus create(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        return readStatusService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ReadStatus update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, request);
    }
}
