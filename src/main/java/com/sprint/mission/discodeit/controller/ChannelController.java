
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPublic(
            @RequestBody PublicChannelCreateRequest request
    ) {
        Channel channel = channelService.create(request);
        return channelService.find(channel.getId());
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPrivate(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        Channel channel = channelService.create(request);
        return channelService.find(channel.getId());
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ChannelDto updatePublic(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        channelService.update(channelId, request);
        return channelService.find(channelId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> findAll(
            @RequestParam("userId") UUID userId
    ) {
        return channelService.findAllByUserId(userId);
    }
}
