package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class MessageCreateRequestDto {

    @Schema(example = "메시지 내용 예시입니다.")
    private String content;

    @NotNull(message = "채널 ID는 필수입니다.")
    private UUID channelId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private UUID authorId;
}
