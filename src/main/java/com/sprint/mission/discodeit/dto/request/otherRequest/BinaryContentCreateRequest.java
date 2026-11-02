package com.sprint.mission.discodeit.dto.request.otherRequest;

public record BinaryContentCreateRequest(String fileName,
                                         String contentType,
                                         Long size,
                                         byte[] bytes) { }