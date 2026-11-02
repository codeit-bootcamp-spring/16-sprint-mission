package com.sprint.mission.discodeit.dto.Message;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;

import java.util.List;

public record MessageAttachmentsCreateRequest(
        List<BinaryContentCreateRequest> files
) {
}
