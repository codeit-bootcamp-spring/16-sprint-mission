
package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;

public class MessageResponseMapper {

    private MessageResponseMapper() {
    }

    public static MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getUserId(),
                message.getChannelId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getAttachmentIds()
        );
    }
}
