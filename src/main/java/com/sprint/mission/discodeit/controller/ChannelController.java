package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channels.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성(PUBLIC)-POST
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelDto postPublicChannel(@Valid @RequestBody PublicChannelCreateRequest request) {
        return channelService.createPublicChannel(request);
    }

    // 비공개 채널 생성(PRIVATE)-POST
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelDto postPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequest request) {
        return channelService.createPrivateChannel(request);
    }

    // 채널 단건 조회(GET)
    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ChannelDto getChannel(@PathVariable UUID channelId) {
        return channelService.readChannel(channelId);
    }

    // 전체 채널 목록 조회(GET)
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<ChannelDto> findAllChannels() {
        return channelService.findAll();
    }

    // 특정 사용자가 볼 수 있는 모든 채널 조회(GET)
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public List<ChannelDto> getChannelByUserId(@PathVariable UUID userId){
        return channelService.findAllByUserId(userId);
    }

    // 공개 채널 정보 수정(PUT)
    // 수정할 때 본인을 제외한 다른 유저의 이름이나 이메일을 수정할 경우 "409" 가 발생한다.
    @RequestMapping(method = RequestMethod.PUT)
    public ChannelDto putChannel(@Valid @RequestBody ChannelUpdateRequest request) {
        return channelService.updateChannel(request);
    }

    // 채널 삭제(DELETE)
    @RequestMapping(value = "/{channelId}",method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.deleteChannel(channelId);
    }
}