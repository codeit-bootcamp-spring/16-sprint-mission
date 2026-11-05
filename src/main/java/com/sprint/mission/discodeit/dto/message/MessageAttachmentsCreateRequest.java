package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;

import java.util.List;

public record MessageAttachmentsCreateRequest(
        List<BinaryContentCreateRequest> files
) {
}
