package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public record MessageCreateDto(
        String content,
        UUID authorId,
        UUID channelId,
        List<BinaryContentDto> attachments
) {
}
