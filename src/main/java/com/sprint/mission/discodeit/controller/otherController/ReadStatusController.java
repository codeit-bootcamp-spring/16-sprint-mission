package com.sprint.mission.discodeit.controller.otherController;

import com.sprint.mission.discodeit.dto.request.status.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.status.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;
import com.sprint.mission.discodeit.service.otherService.ReadStatusService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성(POST)
    @RequestMapping(method = RequestMethod.POST)
    public ReadStatus postReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    // 특정 채널의 메시지 수신 정보 수정(PUT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ReadStatus putReadStatus(@PathVariable UUID id,
                                    @Valid @RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.update(id, request);
    }

    // 특정 사용자의 메시지 수신 정보 조회(GET)
    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> getAllByUserId(@RequestParam UUID userID) {
        return readStatusService.findAllByUserId(userID);
    }
}
