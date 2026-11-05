package com.sprint.mission.discodeit.practice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
