package com.sprint.mission.discodeit.dto.request.messages;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest (String content,
                                    UUID userId,
                                    UUID channelId,
                                    List<BinaryContentCreateRequest> attachments) { }