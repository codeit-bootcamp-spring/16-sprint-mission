package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.PublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.CustomErrorResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성된 공개 채널 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Channel.class),
                            examples = @ExampleObject(value = "{\"id\":\"6dd80dd7-c1b2-4690-ac2a-9f58aefda3a2\","
                                    + "\"createdAt\":\"2026-11-16T02:43:46Z\",\"updatedAt\":\"2026-11-16T02:43:46Z\","
                                    + "\"type\":\"PUBLIC\",\"name\":\"general\",\"description\":\"자유 대화 채널\"}"))),
            @ApiResponse(responseCode = "400", description = "요청 JSON을 읽을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 공개 채널 이름인 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"409\",\"message\":\"사용 중인 채널명\"}")))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST,value = "/public")
    public Channel createPublic(@RequestBody PublicChannelRequest request){
        return channelService.createPublic(request);
    }

    @Operation(summary = "비공개 채널 생성", description = "지정한 사용자를 참여자로 등록하고 생성된 비공개 채널 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성된 비공개 채널 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Channel.class))),
            @ApiResponse(responseCode = "400", description = "요청 JSON을 읽을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "참여자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"404\",\"message\":\"유저를 찾을 수 없음\"}")))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST,value = "/private")
    public Channel createPrivate(@RequestBody PrivateChannelRequest request){
        return channelService.createPrivate(request);
    }

    @Operation(summary = "공개 채널 수정", description = "채널 ID로 공개 채널을 찾아 이름과 설명을 수정하고 수정된 채널 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정된 공개 채널 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Channel.class))),
            @ApiResponse(responseCode = "400", description = "비공개 채널 수정 요청 또는 잘못된 채널 ID나 요청 JSON",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"400\",\"message\":\"PRIVATE 채널은 수정할 수 없음\"}"))),
            @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"404\",\"message\":\"채널을 찾을 수 없음\"}"))),
            @ApiResponse(responseCode = "409", description = "다른 공개 채널이 이미 같은 이름을 사용 중인 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"409\",\"message\":\"사용 중인 채널명\"}")))
    })
    @RequestMapping(method = RequestMethod.PATCH,value = "/{channelId}")
    public Channel update(@PathVariable UUID channelId, @RequestBody ChannelUpdateRequest request){
        ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(channelId, request.newName(), request.newDescription());
        return channelService.update(channelUpdateRequest);
    }

    @Operation(summary = "채널 삭제", description = "채널 ID로 채널을 찾아 관련 메시지와 첨부파일 및 읽음 상태를 함께 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "채널 삭제 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "채널 ID의 UUID 형식이 잘못된 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"404\",\"message\":\"채널을 찾을 수 없음\"}")))
    })
    @RequestMapping(method = RequestMethod.DELETE,value = "{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID channelId){
        channelService.delete(channelId);
    }

    @Operation(summary = "채널 목록 조회", description = "공개 채널과 해당 사용자가 참여한 비공개 채널을 목록으로 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회할 수 있는 채널 목록을 반환하고 없으면 빈 배열 반환"),
            @ApiResponse(responseCode = "400", description = "사용자 ID가 누락되었거나 UUID 형식이 잘못된 경우")
    })
    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> findAllByUserId(@RequestParam("userId") UUID userId){
        return channelService.findAllByUserId(userId);
    }
}