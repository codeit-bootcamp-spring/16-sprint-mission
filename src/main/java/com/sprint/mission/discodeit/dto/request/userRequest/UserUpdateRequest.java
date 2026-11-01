package com.sprint.mission.discodeit.dto.request.userRequest;

import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;

import java.util.UUID;

public record UserUpdateRequest(UUID userId,
                                String username,
                                String email,
                                String password,
                                BinaryContent profile) { }