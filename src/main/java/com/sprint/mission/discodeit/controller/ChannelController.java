package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.PublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "채널", description = "채널 생성과 조회와 수정 및 삭제 API")
public class ChannelController {
    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Operation(summary = "공개 채널 생성", description = "채널 이름과 설명을 받아 공개 채널을 생성하고 생성된 채널 정보 반환")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST,value = "/public")
    public Channel createPublic(@RequestBody PublicChannelRequest request){
        return channelService.createPublic(request);
    }

    @Operation(summary = "비공개 채널 생성", description = "지정한 사용자를 참여자로 등록하고 생성된 비공개 채널 정보 반환")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST,value = "/private")
    public Channel createPrivate(@RequestBody PrivateChannelRequest request){
        return channelService.createPrivate(request);
    }

    @Operation(summary = "공개 채널 수정", description = "채널 ID로 공개 채널을 찾아 이름과 설명을 수정하고 수정된 채널 정보 반환")
    @RequestMapping(method = RequestMethod.PATCH,value = "/{channelId}")
    public Channel update(@PathVariable UUID channelId, @RequestBody ChannelUpdateRequest request){
        ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(channelId, request.newName(), request.newDescription());
        return channelService.update(channelUpdateRequest);
    }

    @Operation(summary = "채널 삭제", description = "채널 ID로 채널을 찾아 관련 메시지와 첨부파일 및 읽음 상태를 함께 삭제")
    @RequestMapping(method = RequestMethod.DELETE,value = "{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID channelId){
        channelService.delete(channelId);
    }

    @Operation(summary = "채널 목록 조회", description = "공개 채널과 해당 사용자가 참여한 비공개 채널을 목록으로 반환")
    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> findAllByUserId(@RequestParam("userId") UUID userId){
        return channelService.findAllByUserId(userId);
    }
}