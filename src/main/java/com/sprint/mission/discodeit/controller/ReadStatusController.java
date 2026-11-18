package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStatus.*;
import com.sprint.mission.discodeit.service.basic.ReadStatusService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/read-status")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusFindResponse>> findAllReadStatusByUserId(@NotNull @PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.readStatusService.findAllByUserId(userId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusCreateResponse> createReadStatus(@Valid @RequestBody ReadStatusCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.readStatusService.create(createRequest));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusUpdateResponse> updateReadStatus(@NotNull @PathVariable UUID id,
                                                                     @Valid @RequestBody ReadStatusUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.readStatusService.update(id, updateRequest));
    }
}
