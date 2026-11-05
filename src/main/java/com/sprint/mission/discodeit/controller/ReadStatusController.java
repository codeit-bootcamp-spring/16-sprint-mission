package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController // 반환한 수신 정보를 JSON으로 응답합니다.
@RequestMapping("/api/read-statuses") // 수신 정보 API의 공통 주소입니다.
@RequiredArgsConstructor // ReadStatusService를 주입받는 생성자를 만듭니다.
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 1. 특정 채널의 메시지 수신 정보 생성
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        // 사용자·채널의 존재와 동일 조합의 중복 여부를 검사하고 저장합니다.
        ReadStatus readStatus = readStatusService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    // 2. 특정 채널의 메시지 수신 정보 수정
    @RequestMapping(
            value = "/{readStatusId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> update(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        // URL의 수신 정보 ID와 JSON의 마지막 읽은 시각을 합칩니다.
        ReadStatusUpdateRequest serviceRequest = new ReadStatusUpdateRequest(
                readStatusId,
                request.lastReadAt()
        );

        // 기존 수신 정보의 마지막 읽은 시각을 변경합니다.
        readStatusService.update(serviceRequest);

        // update()는 반환값이 없으므로 수정 결과를 다시 조회합니다.
        ReadStatus readStatus = readStatusService.findById(readStatusId);

        return ResponseEntity.ok(readStatus);
    }

    // 3. 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam("userId") UUID userId // ?userId= 값을 받습니다.
    ) {
        // 해당 사용자의 채널별 수신 정보 목록을 조회합니다.
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(readStatuses);
    }
}