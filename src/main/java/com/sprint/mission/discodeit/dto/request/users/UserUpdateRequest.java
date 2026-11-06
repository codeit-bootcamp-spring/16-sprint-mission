package com.sprint.mission.discodeit.dto.request.users;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;

import java.util.UUID;

public record UserUpdateRequest(UUID userId,
                                String username,
                                String email,
                                String password,
                                BinaryContentCreateRequest profile) { }