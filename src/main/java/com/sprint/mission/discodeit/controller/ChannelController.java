package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final ChannelMapper channelMapper;
    /* 아래 createChannel() 메서드는 비즈니스 로직이 과하게 들어가있기 때문에 사용하지 않습니다.
    @PostMapping
    // 현재는, PrivateChannelCreateRequest DTO와 PublicChannelCreateRequest DTO 만
    // 존재하기 때문에, ChannelCreateRequest DTO 가 새로 필요합니다.
    // Java의 참조 변수 타입과 Jackson이 직렬화할 때 사용하는 실제 객체 타입은 다를 수 있습니다.
    // Java 타입 관점: ChannelInfo 타입의 참조 변수로는 인터페이스에 선언되지 않은 메서드를 직접 호출할 수 없습니다.
    // JSON 직렬화 관점: Jackson은 일반적으로 실제 구현체 객체를 기준으로 직렬화하므로,
    // 인터페이스에 없는 필드도 JSON 응답에 포함될 수 있습니다.
    public ResponseEntity<ChannelInfo> createChannel(@RequestBody ChannelCreateRequest request) {
        Channel channel = null;
        if (request.userList() != null && request.name() == null && request.description() == null) {

            // 컨트롤러 메서드 내부에서 DTO 객체를 생성하는 것과, 컨트롤러 빈이 상태를 가지는 것은 다릅니다.
            // 지역 변수는, 메서드가 실행될 때 마다 별도로 관리되므로, 컨트롤러 빈의 상태가 되지않습니다(공유 가변 상태 x)
            // 인스턴스 필드는, 컨트롤러 객체가 참조하므로, 빈의 상태가 될 수 있습니다.

            PrivateChannelCreateRequest privateChannelCreateRequest =
                    new PrivateChannelCreateRequest(request.userList());
            channel = this.channelService.createPrivateChannel(privateChannelCreateRequest);
        } else if (request.userList() == null && request.name() != null) {
            PublicChannelCreateRequest publicChannelCreateRequest =
                    new PublicChannelCreateRequest(request.name(), request.description());
            channel = this.channelService.createPublicChannel(publicChannelCreateRequest);
        } else {
            throw new IllegalArgumentException("옳바르지 않은 채널 생성 요청입니다. ");
        }

        Instant lastMessageAt = this.channelService.findLastMessageAt(channel.getId());

        return ResponseEntity.ok(this.channelMapper.toChannelInfo(channel, lastMessageAt));
    }
     */

    @PostMapping
    public ResponseEntity<ChannelInfo> createChannel(@RequestBody ChannelCreateRequest request) {
        ChannelInfo channelInfo = this.channelService.createChannel(request);

        return ResponseEntity.ok(channelInfo);
    }

    @PatchMapping("/{channel-id}")
    public ResponseEntity<ChannelInfo> updatePublicChannel(@PathVariable("channel-id") UUID channelId, @RequestBody ChannelUpdateRequest request) {
        ChannelInfo channelInfo = this.channelService.update(channelId, request);

        return ResponseEntity.ok(channelInfo);
    }

    @DeleteMapping("/{channel-id}")
    public ResponseEntity<ChannelInfo> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        this.channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<ChannelInfoList> getVisibleChannels(@PathVariable("user-id") UUID userId) {
        List<ChannelInfo> channelInfoList = this.channelService.findAllByUserId(userId);
        ChannelInfoList channelInfoListDto = new ChannelInfoList(channelInfoList);

        return ResponseEntity.ok(channelInfoListDto);
    }
}
