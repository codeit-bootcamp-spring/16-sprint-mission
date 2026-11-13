package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {
  @Operation(summary = "Public Channel 생성")
  @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
  ResponseEntity<ChannelDto> createPublic(PublicChannelCreateRequest request);

  @Operation(summary = "Private Channel 생성")
  @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
  ResponseEntity<ChannelDto> createPrivate(PrivateChannelCreateRequest request);

  @Operation(summary = "Channel 삭제")
  @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨")
  @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음")
  ResponseEntity<Void> delete(@Parameter(description = "삭제할 Channel ID") UUID channelId);

  @Operation(summary = "Channel 정보 수정")
  @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨")
  @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음")
  @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음")
  ResponseEntity<ChannelDto> update(
      @Parameter(description = "수정할 Channel ID") UUID channelId, ChannelUpdateRequest request);

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
  ResponseEntity<List<ChannelDto>> findAllByUserId(
      @Parameter(description = "조회할 User ID") UUID userId);
}
