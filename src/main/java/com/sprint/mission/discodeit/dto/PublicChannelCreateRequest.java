package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class PublicChannelCreateRequest {
    private final String name;
    private final String description;

    public PublicChannelCreateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

