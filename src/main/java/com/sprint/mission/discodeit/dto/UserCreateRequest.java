package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class UserCreateRequest {
    private final String username;
    private final String email;
    private final String password;

    private final String profileFileName;
    private final String profileContentType;
    private final byte[] profileData;

    public UserCreateRequest(
            String username,
            String email,
            String password,
            String profileFileName,
            String profileContentType,
            byte[] profileData
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileFileName = profileFileName;
        this.profileContentType = profileContentType;
        this.profileData = profileData;
    }
}