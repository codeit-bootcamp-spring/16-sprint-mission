package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비공개 채널 생성 요청")
public record PrivateChannelCreateRequest(
        @Schema(description = "채널에 참여할 사용자 UUID 목록")
        List<UUID> participantIds
) {
}