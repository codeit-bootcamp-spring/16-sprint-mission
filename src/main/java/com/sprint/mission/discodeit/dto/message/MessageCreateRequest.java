package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        @NotBlank
        String content,
        @NotNull
        UUID channelId,
        @NotNull
        UUID authorId
) {
}
