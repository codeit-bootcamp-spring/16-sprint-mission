package com.sprint.mission.discodeit.dto.request.messages;

import java.util.UUID;

public record MessageUpdateRequest (UUID messageId,
                                    UUID requesterId,
                                    String content) { }