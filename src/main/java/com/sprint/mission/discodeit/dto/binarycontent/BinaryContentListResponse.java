package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.List;

public record BinaryContentListResponse(
        List<BinaryContentResponse> binaryContentResponseList
) {
}
