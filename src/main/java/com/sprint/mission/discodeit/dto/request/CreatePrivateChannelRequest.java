package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비공개 채널 생성 요청")
public record CreatePrivateChannelRequest(
        @Schema(description = "참여자(사용자) id 목록", example = "[\"11111111-1111-4111-8111-111111111111\"]")
        List<UUID> participantIds
) {}
