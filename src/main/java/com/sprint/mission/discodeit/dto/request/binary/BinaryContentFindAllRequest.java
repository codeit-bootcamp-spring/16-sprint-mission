package com.sprint.mission.discodeit.dto.request.binary;

import java.util.List;
import java.util.UUID;

public record BinaryContentFindAllRequest(List<UUID> ids) { }