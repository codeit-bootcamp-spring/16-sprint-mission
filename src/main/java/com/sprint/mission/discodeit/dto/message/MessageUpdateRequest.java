package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record MessageUpdateRequest(
        String newContent

        // @Nullable List<BinaryContent> newBinaryContents
) {
}
