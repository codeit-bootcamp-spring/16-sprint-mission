package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelUpdateRequestDto {

    @Schema(example = "채널 이름 수정 예시")
    private String newName;

    @Schema(example = "채널 설명 수정 예시입니다.")
    private String newDescription;
}
