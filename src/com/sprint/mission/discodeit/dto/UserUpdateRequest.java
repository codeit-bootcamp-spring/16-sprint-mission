package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        String username,
        String email,
        String password,
        String profileImageName,
        String profileImageContentType,
        Long profileImageSize,
        byte[] profileImageData
) {
    public UserUpdateRequest(
            UUID id,
            String username,
            String email,
            String password,
            String profileImageName,
            String profileImageContentType,
            Long profileImageSize
    ) {
        this(
                id,
                username,
                email,
                password,
                profileImageName,
                profileImageContentType,
                profileImageSize,
                null
        );
    }

    public UserUpdateRequest {
        if (profileImageData != null) {
            profileImageData = profileImageData.clone();
        }
    }

    @Override
    public byte[] profileImageData() {
        return profileImageData == null
                ? null
                : profileImageData.clone();
    }
}