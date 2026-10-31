package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateRequest {
    private final UUID userId;

    private final String username;
    private final String email;
    private final String password;

    private final String profileFileName;
    private final String profileContentType;
    private final byte[] profileData;

    public UserUpdateRequest(
            UUID userId,
            String username,
            String email,
            String password,
            String profileFileName,
            String profileContentType,
            byte[] profileData
    ) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileFileName = profileFileName;
        this.profileContentType = profileContentType;
        this.profileData = profileData;
    }
}