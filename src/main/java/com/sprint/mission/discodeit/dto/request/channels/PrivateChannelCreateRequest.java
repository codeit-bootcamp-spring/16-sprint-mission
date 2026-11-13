package com.sprint.mission.discodeit.dto.request.channels;

import jakarta.validation.constraints.NotEmpty; // null, 빈 문자열 방지, 공백은 허용

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @NotEmpty(message = "참여자 ID가 필요합니다.")
        List<UUID> memberIds) { }