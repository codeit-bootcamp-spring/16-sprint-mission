package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public final class ApiSpecDto {

    private ApiSpecDto() {
    }

    @Schema(name = "UserCreateRequest")
    public record UserCreate(
            String username,
            String email,
            String password
    ) {
    }

    @Schema(name = "UserUpdateRequest")
    public record UserUpdate(
            String newUsername,
            String newEmail,
            String newPassword
    ) {
    }

    @Schema(name = "UserStatusUpdateRequest")
    public record UserStatusUpdate(
            Instant newLastActiveAt
    ) {
    }

    @Schema(name = "PublicChannelCreateRequest")
    public record PublicChannelCreate(
            String name,
            String description
    ) {
    }

    @Schema(name = "PrivateChannelCreateRequest")
    public record PrivateChannelCreate(
            Set<UUID> participantIds
    ) {
    }

    @Schema(name = "PublicChannelUpdateRequest")
    public record PublicChannelUpdate(
            String newName,
            String newDescription
    ) {
    }

    @Schema(name = "MessageCreateRequest")
    public record MessageCreate(
            String content,
            UUID channelId,
            UUID authorId
    ) {
    }

    @Schema(name = "MessageUpdateRequest")
    public record MessageUpdate(
            String newContent
    ) {
    }

    @Schema(name = "ReadStatusCreateRequest")
    public record ReadStatusCreate(
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {
    }

    @Schema(name = "ReadStatusUpdateRequest")
    public record ReadStatusUpdate(
            Instant newLastReadAt
    ) {
    }

    @Schema(name = "User")
    public record UserView(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            UUID profileId
    ) {
    }

    @Schema(name = "Channel")
    public record ChannelView(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description
    ) {
    }

    @Schema(name = "ChannelDto")
    public record ChannelSummary(
            UUID id,
            ChannelType type,
            String name,
            String description,
            Set<UUID> participantIds,
            Instant lastMessageAt
    ) {
    }

    @Schema(name = "Message")
    public record MessageView(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String content,
            UUID channelId,
            UUID authorId,
            Set<UUID> attachmentIds
    ) {
    }

    @Schema(name = "UserStatus")
    public record UserStatusView(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            Instant lastActiveAt,
            boolean online
    ) {
    }
}