package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponseDto toResponseDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }
}
