package com.sprint.mission.discodeit.dto.request.userRequest;

import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;

public record UserCreateRequest(String username,
                                String email,
                                String password,
                                BinaryContent profile) { }