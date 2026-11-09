package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/read-status")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @PostMapping("/create")
    public ResponseEntity<ReadStatusResponse> createReadStatus(@RequestBody CreateReadStatusRequest request) {
        ReadStatusResponse response = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<ReadStatusResponse> updateReadStatus(@RequestBody UpdateReadStatusRequest request) {
        ReadStatusResponse response = readStatusService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<List<ReadStatusResponse>> getReadStatus(@PathVariable("user-id") UUID userId) {
        List<ReadStatusResponse> response = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
