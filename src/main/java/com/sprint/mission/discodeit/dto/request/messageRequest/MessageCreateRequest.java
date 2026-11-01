package com.sprint.mission.discodeit.dto.request.messageRequest;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest (String content,
                                    UUID userId,
                                    UUID channelId,
                                    List<UUID> attachmentIds) { }