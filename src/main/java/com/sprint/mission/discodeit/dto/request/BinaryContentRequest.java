package com.sprint.mission.discodeit.dto.request;

public record BinaryContentRequest(
   String fileName,
   String contentType,
   byte[] bytes
) {}
