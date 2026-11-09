package com.sprint.mission.discodeit.dto;

public record CreateBinaryContentRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}