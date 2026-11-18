package com.sprint.mission.discodeit.dto;


import io.swagger.v3.oas.annotations.media.Schema;

public record UserCreateRequest (@Schema(description = "등록할 사용자 이름", example = "demo-user") String username,
                                 @Schema(description = "등록할 비밀번호", example = "demo-password") String password,
                                 @Schema(description = "등록할 이메일 주소", example = "demo@example.com") String email){
}
