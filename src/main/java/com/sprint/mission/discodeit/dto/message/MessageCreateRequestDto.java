package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String content;

    @NotNull(message = "채널 ID는 필수입니다.")
    private UUID channelId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private UUID authorId;
}
