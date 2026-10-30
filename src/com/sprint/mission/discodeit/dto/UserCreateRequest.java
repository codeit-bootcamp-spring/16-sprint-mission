package com.sprint.mission.discodeit.dto;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        String profileImageName,
        String profileImageContentType,
        Long profileImageSize,
        byte[] profileImageData
) {
    public UserCreateRequest(
            String username,
            String email,
            String password,
            String profileImageName,
            String profileImageContentType,
            Long profileImageSize
    ) {
        this(
                username,
                email,
                password,
                profileImageName,
                profileImageContentType,
                profileImageSize,
                null
        );
    }

    public UserCreateRequest {
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