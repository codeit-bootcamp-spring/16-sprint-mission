package com.sprint.mission.discodeit.dto.binaryContent;

import java.util.List;
import java.util.UUID;

public record BinaryContentFindAllByIdInRequest(
        List<UUID> ids
) {
}
