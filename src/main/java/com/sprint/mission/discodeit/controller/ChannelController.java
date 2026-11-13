package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {
  private final ChannelService channelService;

  @Override
  @PostMapping(value = "/public")
  public ResponseEntity<ChannelDto> createPublic(@RequestBody PublicChannelCreateRequest request) {
    ChannelDto channel = channelService.createPublic(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  @Override
  @PostMapping(value = "/private")
  public ResponseEntity<ChannelDto> createPrivate(
      @RequestBody PrivateChannelCreateRequest request) {
    ChannelDto channel = channelService.createPrivate(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  @Override
  @PatchMapping(value = "/{channelId}")
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId, @RequestBody ChannelUpdateRequest request) {
    return ResponseEntity.ok(channelService.update(channelId, request));
  }

  @Override
  @DeleteMapping(value = "/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }

  @PostMapping(value = "/{channelId}/members")
  public ResponseEntity<Void> join(@PathVariable UUID channelId, @RequestParam UUID userId) {
    channelService.join(channelId, userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{channelId}/members")
  public ResponseEntity<Void> leave(@PathVariable UUID channelId, @RequestParam UUID userId) {
    channelService.leave(channelId, userId);
    return ResponseEntity.noContent().build();
  }
}
