package com.sprint.mission.discodeit.exceptions;

// 파일 처리 중 발생한 예외를 나타내는 사용자 정의 예외 클래스이다.
public class FileProcessingException extends RuntimeException {

    // 예외 메시지와 실제 원인이 된 예외 정보를 전달받아 부모 클래스에 저장한다.
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
