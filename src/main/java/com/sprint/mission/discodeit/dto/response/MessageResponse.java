package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 정보")
public record MessageResponse(
        @Schema(description = "메시지 id", example = "33333333-3333-4333-8333-333333333333")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-11-13T00:00:00Z")
        String createdAt,
        @Schema(description = "마지막 수정 시각", example = "2026-11-13T01:00:00Z")
        String updatedAt,
        @Schema(description = "메시지 내용", example = "안녕하세요")
        String content,
        @Schema(description = "채널 id", example = "22222222-2222-4222-8222-222222222222")
        UUID channelId,
        @Schema(description = "작성자(사용자) id", example = "11111111-1111-4111-8111-111111111111")
        UUID authorId,
        @Schema(description = "첨부파일(BinaryContent) id 목록", example = "[\"66666666-6666-4666-8666-666666666666\"]")
        List<UUID> attachmentIds
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(message.getId(), message.getCreatedAt().toString(),
                message.getUpdatedAt().toString(), message.getContent(), message.getChannelId(),
                message.getAuthorId(), message.getAttachmentIds());
    }
}
