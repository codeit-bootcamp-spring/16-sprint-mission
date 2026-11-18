package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.MessageType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class MessageResponseDto {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    @Schema(example = "메시지 내용 예시입니다.")
    private String content;

    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;
}