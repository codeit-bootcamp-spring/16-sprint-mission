package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChannelUpdateRequest(
    @Pattern(regexp = ".*\\S.*", message = "공백만 입력할 수 없습니다.") @Size(max = 100) String newName,
    @Size(max = 1024) String newDescription) {}
