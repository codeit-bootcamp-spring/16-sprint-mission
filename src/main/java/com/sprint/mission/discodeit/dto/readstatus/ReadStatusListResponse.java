package com.sprint.mission.discodeit.dto.readstatus;

import java.util.List;

public record ReadStatusListResponse(
        List<ReadStatusResponse> readStatuses
) {
}
