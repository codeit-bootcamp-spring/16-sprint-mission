package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivateChannelCreateRequestDto {

    @NotEmpty(message = "참여자는 최소 한 명 이상이어야 합니다.")
    private List<UUID> participantIds;
}