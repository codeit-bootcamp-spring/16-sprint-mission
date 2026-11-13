package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {
  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  ResponseEntity<List<MessageDto>> findAllByChannelId(
      @Parameter(description = "조회할 Channel ID") UUID channelId);

  @Operation(summary = "Message 생성")
  @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨")
  @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음")
  ResponseEntity<MessageDto> create(
      MessageCreateRequest messageCreateRequest,
      @Parameter(description = "Message 첨부 파일들") List<MultipartFile> attachments);

  @Operation(summary = "Message 삭제")
  @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨")
  @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  ResponseEntity<Void> delete(@Parameter(description = "삭제할 Message ID") UUID messageId);

  @Operation(summary = "Message 내용 수정")
  @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨")
  @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  ResponseEntity<MessageDto> update(
      @Parameter(description = "수정할 Message ID") UUID messageId, MessageUpdateRequest request);
}
