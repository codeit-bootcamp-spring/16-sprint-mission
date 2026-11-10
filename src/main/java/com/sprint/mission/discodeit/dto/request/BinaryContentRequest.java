package com.sprint.mission.discodeit.dto.request;

public record BinaryContentRequest(
        String filename,
        Long size,
        String contentType,
        byte[] bytes
) {}
