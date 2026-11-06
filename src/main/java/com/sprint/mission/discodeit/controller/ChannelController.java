package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.channels.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성(PUBLIC)
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelDto postPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        return channelService.createPublicChannel(request);
    }

    // 공개 채널 생성(PUBLIC)
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelDto postPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.createPrivateChannel(request);
    }

    // 공개 채널 정보 수정(PUT)
    @RequestMapping(method = RequestMethod.PUT)
    public ChannelDto putChannel(@RequestBody ChannelUpdateRequest request) {
        return channelService.updateChannel(request);
    }

    // 채널 삭제(DELETE)
    @RequestMapping(value = "/{channelId}",method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.deleteChannel(channelId);
    }

    // 특정 사용자가 볼 수 있는 모든 채널 조회(GET)
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public List<ChannelDto> getChannelByUserId(@PathVariable UUID userId){
        return channelService.findAllByUserId(userId);
    }
}
