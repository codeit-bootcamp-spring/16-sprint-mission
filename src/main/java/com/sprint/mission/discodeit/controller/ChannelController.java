package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.PublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/public")
    public Channel createPublic(@RequestBody PublicChannelRequest request){
        return channelService.createPublic(request);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/private")
    public Channel createPrivate(@RequestBody PrivateChannelRequest request){
        return channelService.createPrivate(request);
    }

    @RequestMapping(method = RequestMethod.PATCH,value = "/{channelId}")
    public Channel update(@PathVariable UUID channelId, @RequestBody ChannelUpdateRequest request){
        ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(channelId,request.name(),request.description());
        return channelService.update(channelUpdateRequest);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID channelId){
        channelService.delete(channelId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> findAllByUserId(@RequestParam("userId") UUID userId){
        return channelService.findAllByUserId(userId);
    }
}
