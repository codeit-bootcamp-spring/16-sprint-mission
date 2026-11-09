package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatus createReadStatus(@RequestBody ReadStatusCreateRequest request){
        return readStatusService.create(request);
    }

    @RequestMapping(method = RequestMethod.PATCH,value = "/{readStatusId}")
    public ReadStatus updateReadStatus(@PathVariable UUID readStatusId,@RequestBody ReadStatusUpdateRequest request){
        ReadStatusUpdateRequest readStatusUpdateRequest = new ReadStatusUpdateRequest(readStatusId,request.lastReadAt());
        return readStatusService.update(readStatusUpdateRequest);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> findAllReadStatuses(@RequestParam("userId")UUID userId){
        return readStatusService.findAllByUserId(userId);
    }
}
