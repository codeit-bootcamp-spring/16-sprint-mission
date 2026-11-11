package com.sprint.mission.discodeit.dto;

public record BinaryContentDto(
        String filename,
        Long size,
        byte[] bytes
) {
}
