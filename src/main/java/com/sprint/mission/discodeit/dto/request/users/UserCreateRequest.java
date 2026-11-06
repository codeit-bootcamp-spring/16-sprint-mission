package com.sprint.mission.discodeit.dto.request.users;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;

public record UserCreateRequest(String username,
                                String email,
                                String password,
                                BinaryContentCreateRequest profile) { }