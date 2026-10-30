package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.MessageType;
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
    private MessageType type;
    private String content;
    private UUID senderId;
    private UUID channelId;
    private List<UUID> attachmentIds;
    private Instant createdAt;
    private Instant updatedAt;
}