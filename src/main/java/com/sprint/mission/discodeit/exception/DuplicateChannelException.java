package com.sprint.mission.discodeit.exception;

public class DuplicateChannelException extends RuntimeException {
    public DuplicateChannelException() {
        super("사용중인 채널명");
    }
}
