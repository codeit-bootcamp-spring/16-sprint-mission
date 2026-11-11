package com.sprint.mission.discodeit.dto.request.messageRequest;

import com.sprint.mission.discodeit.dto.request.otherRequest.BinaryContentCreateRequest;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest (String content,
                                    UUID userId,
                                    UUID channelId,
                                    List<BinaryContentCreateRequest> attachments) { }