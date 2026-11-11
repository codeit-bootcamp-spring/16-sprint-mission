package com.sprint.mission.discodeit.dto;

public record UserCreateDto (
    String username,
    String email,
    String password,

    String fileName,
    String contentType,
    Long fileSize,
    String fileUrl
)
{
    public boolean hasProfileImage(){
        return fileUrl!= null&& !fileUrl.isBlank();
    }
}

