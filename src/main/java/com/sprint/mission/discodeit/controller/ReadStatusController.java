package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.ReadStatusControllerDocs;
import com.sprint.mission.discodeit.dto.readStatus.*;
import com.sprint.mission.discodeit.service.basic.ReadStatusService;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("api/readStatuses")
public class ReadStatusController implements ReadStatusControllerDocs {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> findAllReadStatusByUserId(@Parameter(description = "사용자 ID") @NotNull @RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.readStatusService.findAllByUserId(userId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> createReadStatus(@Valid @RequestBody ReadStatusCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.readStatusService.create(createRequest));
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponse> updateReadStatus(@Parameter(description = "메세지 읽음 상태 ID") @NotNull @PathVariable UUID readStatusId,
                                                               @Valid @RequestBody ReadStatusUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.readStatusService.update(readStatusId, updateRequest));
    }
}
