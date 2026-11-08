package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.PublicChannelRequestDto;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;


    @PostMapping("/publc")
    public ResponseEntity<ChannelResponseDto> createPublicChannel(@RequestBody PublicChannelRequestDto publicChannelRequestDto) {
        ChannelResponseDto channelResponseDto = channelService.createPublicChannel(publicChannelRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponseDto);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(@RequestBody PrivateChannelRequestDto privateChannelRequestDto) {
        ChannelResponseDto channelResponseDto = channelService.createPrivateChannel(privateChannelRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponseDto);
    }

    @PatchMapping("/public/{channelId}")
    public ResponseEntity<ChannelResponseDto> updatePublicChannel(@PathVariable UUID channelId, @RequestBody PublicChannelUpdateDto publicChannelUpdateDto) {
        ChannelResponseDto channelResponseDto = channelService.update(channelId, publicChannelUpdateDto);
        return ResponseEntity.ok(channelResponseDto);
    }

    @DeleteMapping("/{channelId}")
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ChannelResponseDto>> findAllChannelByUserId(@PathVariable UUID userId) {
        List<ChannelResponseDto> channelResponseDtoList = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channelResponseDtoList);
    }
}
