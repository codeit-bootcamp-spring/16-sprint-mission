package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        @NotNull
        UUID readStatusId
) {
}
