package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadStatusCreateRequestDto {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private UUID userId;

    @NotNull(message = "채널 ID는 필수입니다.")
    private UUID channelId;
}