package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserDto (UUID id, String name, String email,UUID profileId,boolean online){
}
