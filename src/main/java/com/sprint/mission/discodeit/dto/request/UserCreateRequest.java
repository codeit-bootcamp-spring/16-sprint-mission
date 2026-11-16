package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank @Size(max = 32) String username,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(max = 72) String password) {}
