package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.service.basic.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/readstatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponseDto> create(@RequestBody ReadStatusRequestDto readStatusRequestDto) {
        ReadStatusResponseDto readStatusResponseDto = readStatusService.create(readStatusRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusResponseDto);
    }

    @PatchMapping("/{statusId}")
    public ResponseEntity<ReadStatusResponseDto> updateReadStatus(@PathVariable UUID statusId, @RequestBody ReadStatusUpdateDto readStatusUpdateDto) {
        ReadStatusResponseDto readStatusResponseDto = readStatusService.update(statusId, readStatusUpdateDto);
        return ResponseEntity.ok(readStatusResponseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReadStatusResponseDto>> findAllByUserId(@PathVariable UUID userId) {
        List<ReadStatusResponseDto> readStatusResponseDtoList = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatusResponseDtoList);
    }
}
