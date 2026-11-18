package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "채널 내 메시지 및 첨부파일 관리 API")
public interface MessageApi {

    @Operation(summary = "메시지 생성", description = "텍스트 메시지와 선택적 첨부파일 목록을 전송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "메시지 생성 성공"),
            @ApiResponse(responseCode = "400", description = "필수 요청 파트(messageCreateRequest) 누락"),
            @ApiResponse(responseCode = "404", description = "대상 채널 또는 작성자 ID를 찾을 수 없음")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Message> createMessage(
            @Parameter(description = "메시지 생성 요청 JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @Parameter(description = "첨부파일 목록 (선택)")
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments);

    @Operation(summary = "채널별 메시지 목록 조회", description = "특정 채널에 속한 모든 메시지를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메시지 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터(channelId) 누락 또는 형식 오류")
    })
    @GetMapping
    ResponseEntity<List<Message>> findAllByChannelId(
            @Parameter(description = "조회할 채널 UUID") @RequestParam UUID channelId);

    @Operation(summary = "메시지 수정", description = "기존 메시지의 본문 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메시지 수정 성공"),
            @ApiResponse(responseCode = "404", description = "수정할 메시지를 찾을 수 없음")
    })
    @PatchMapping("/{messageId}")
    ResponseEntity<Message> updateMessage(
            @Parameter(description = "수정할 메시지 UUID") @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request);

    @Operation(summary = "메시지 삭제", description = "메시지와 연결된 첨부파일 데이터를 함께 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "메시지 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 메시지를 찾을 수 없음")
    })
    @DeleteMapping("/{messageId}")
    ResponseEntity<Void> deleteMessage(
            @Parameter(description = "삭제할 메시지 UUID") @PathVariable UUID messageId);
}
