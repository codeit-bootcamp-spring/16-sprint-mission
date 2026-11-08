package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.List;
import java.util.UUID;

public record BinaryContentFindAllByIdInRequest(
        List<UUID> binaryContentIds
) {
}
