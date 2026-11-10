package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicChannelCreateRequestDto {

    @NotBlank(message = "채널 이름은 필수입니다.")
    private String channelName;

    private String description;
}