package com.sprint.mission.discodeit.dto.request.userRequest;

import com.sprint.mission.discodeit.dto.request.otherRequest.BinaryContentCreateRequest;

public record UserCreateRequest(String username,
                                String email,
                                String password,
                                BinaryContentCreateRequest profile) { }