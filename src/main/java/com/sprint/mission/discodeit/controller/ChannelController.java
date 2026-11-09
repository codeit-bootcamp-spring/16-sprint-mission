package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor

public class ChannelController {
    private final ChannelService channelService;
    // 공개 채널 생성
    @PostMapping("/public")
    public ChannelDto createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        return channelService.find(channelService.create(request).getId());
    }

    // 비공개 채널 생성
    @PostMapping("/private")
    public ChannelDto createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.find(channelService.create(request).getId());
    }

    // 특정 유저가 입장 가능한 채널 목록 조회
    @GetMapping("/user/{userId}")
    public List<ChannelDto> getChannelsByUserId(@PathVariable UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    // 채널 상세 조회
    @GetMapping("/{channelId}")
    public ChannelDto getChannel(@PathVariable UUID channelId) {
        return channelService.find(channelId);
    }

    // 공개 채널 정보 수정
    @PutMapping("/{channelId}")
    public ChannelDto updateChannel(@PathVariable UUID channelId, @RequestBody PublicChannelUpdateRequest request) {
        return channelService.find(channelService.update(channelId, request).getId());
    }

    // 채널 삭제
    @DeleteMapping("/{channelId}")
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }
}
