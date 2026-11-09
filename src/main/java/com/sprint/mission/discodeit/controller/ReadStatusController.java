package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;

import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read-statuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> create(
            @RequestBody CreateReadStatusRequest request
    ) {
        ReadStatusResponse response =
                readStatusService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatusResponse> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        return readStatusService.findAllByUserId(userId);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public ReadStatusResponse findById(
            @PathVariable("id") UUID id
    ) {
        return readStatusService.findById(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PATCH
    )
    public ReadStatusResponse update(
            @PathVariable("id") UUID id,
            @RequestBody UpdateReadStatusRequest request
    ) {
        if (request.id() != null
                && !id.equals(request.id())) {
            throw new IllegalArgumentException(
                    "URL의 ReadStatus ID와 요청 본문의 ID가 다릅니다."
            );
        }

        UpdateReadStatusRequest updateRequest =
                new UpdateReadStatusRequest(
                        id,
                        request.lastReadAt()
                );

        return readStatusService.update(updateRequest);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID id
    ) {
        readStatusService.delete(id);

        return ResponseEntity.noContent().build();
    }
}