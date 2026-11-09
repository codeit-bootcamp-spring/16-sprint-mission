package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ChannelPrivateCreateRequest(
        @Valid
        List<@NotNull UUID> memberIds
) {
}
