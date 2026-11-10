package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        var created = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.find(created.getId()));
    }
    //비공개 채널 생성
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        var created = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.find(created.getId()));
    }
    //특정 유저가 입장 가능한 채널 목록 조회
    @GetMapping("/users/{userId}/channels")
    public ResponseEntity<List<ChannelDto>> getChannelsByUserId(@PathVariable UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }
    // 채널 상세 조회
    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelDto> getChannel(@PathVariable UUID channelId) {
        ChannelDto channelDto = channelService.find(channelId);
        return ResponseEntity.ok(channelDto);
    }
    // 공개 채널 정보 수정
    @PutMapping("/{channelId}")
    public ResponseEntity<ChannelDto> updateChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        var updated = channelService.update(channelId, request);
        return ResponseEntity.ok(channelService.find(updated.getId()));
    }
    // 채널 삭제
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
