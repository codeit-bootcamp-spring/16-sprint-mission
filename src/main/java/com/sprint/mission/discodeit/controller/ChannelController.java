package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    // 1. 공개 채널 생성
    @RequestMapping(
            value = "/public",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelDto> createChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        Channel channel = channelService.createPublic(request);
        ChannelDto result = channelService.findById(channel.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 2. 비공개 채널 생성
    @RequestMapping(
            value = "/private",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelDto> createPrivate(
            @RequestBody PrivateChannelCreateRequest request
    ) {
         Channel channel = channelService.createPrivate(request);
        ChannelDto result = channelService.findById(channel.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 3. 공개 채널 정보 수정
    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelDto> update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelUpdateRequest serviceRequest = new ChannelUpdateRequest(
                channelId,
                request.name(),
                request.description()
        );
        channelService.update(serviceRequest);

        ChannelDto result = channelService.findById(channelId);

        return ResponseEntity.ok(result);
    }

    // 4. 채널 삭제
    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("channelId") UUID channelId
    ) {
        channelService.delete(channelId);

        return ResponseEntity.noContent().build(); // 204, 응답 본문 없음
    }

    // 5. 특정 사용자가 볼 수 있는 채널 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @RequestParam("userId") UUID userId // 주소의 ?userId= 값을 받습니다.
    ) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channels);
    }
}

