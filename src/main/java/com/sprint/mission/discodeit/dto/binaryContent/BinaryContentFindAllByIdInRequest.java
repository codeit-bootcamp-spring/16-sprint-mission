package com.sprint.mission.discodeit.dto.binaryContent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record BinaryContentFindAllByIdInRequest(
        @Valid
        List<@NotNull UUID> ids
) {
}
