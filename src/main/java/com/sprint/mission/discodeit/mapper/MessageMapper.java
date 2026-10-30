package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

public class MessageMapper {

    public MessageDto toDto(Message message) {

        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getContent(),
                message.getAttachmentIds()
        );
    }
}
