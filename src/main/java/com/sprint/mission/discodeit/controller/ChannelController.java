package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.accept.MediaTypeFileExtensionResolver;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

    private final ChannelService channelService;
    private final MediaTypeFileExtensionResolver mediaTypeFileExtensionResolver;

    @PostMapping("/public")
    @Operation(description = "Public Channel 생성")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
    public ResponseEntity<ChannelDto> create(@RequestBody(required = true) PublicChannelCreateRequest request) {
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PostMapping("/private")
    @Operation(summary = "Private Channel 생성")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
    public ResponseEntity<ChannelDto> create(@RequestBody(required = true) PrivateChannelCreateRequest request) {
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PatchMapping("/{channelId}")
    @Operation(summary = "Channel 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Channel이 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
                    content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "Private channel cannot be updated"))),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    public ResponseEntity<ChannelDto> update(
            @PathVariable("channelId")
            @Parameter(description = "수정할 Channel ID", required = true)
            UUID channelId,
            @RequestBody(required = true) PublicChannelUpdateRequest request) {
        ChannelDto udpatedChannel = channelService.update(channelId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(udpatedChannel);
    }

    @DeleteMapping("/{channelId}")
    @Operation(summary = "Channel 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    public ResponseEntity<Void> delete(
            @PathVariable(value = "channelId", required = true)
            @Parameter(description = "삭제할 Channel ID")
            UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    public ResponseEntity<List<ChannelDto>> findAll(
            @RequestParam("userId")
            @Parameter(required = true, description = "조회할 User ID")
            UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }ㅋ
}
