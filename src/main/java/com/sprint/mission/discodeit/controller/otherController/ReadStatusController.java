package com.sprint.mission.discodeit.controller.otherController;

import com.sprint.mission.discodeit.dto.request.status.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.status.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;
import com.sprint.mission.discodeit.service.otherService.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성(POST)
    @RequestMapping(method = RequestMethod.POST)
    public ReadStatus postReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    // 특정 채널의 메시지 수신 정보 수정(PUT)
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PUT)
    public ReadStatus putReadStatus(@PathVariable UUID readStatusId,
                                    @RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.update(readStatusId, request);
    }

    // 특정 사용자의 메시지 수신 정보 조회(GET)
    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> getAllByUserId(@RequestParam UUID userID) {
        return readStatusService.findAllByUserId(userID);
    }
}
