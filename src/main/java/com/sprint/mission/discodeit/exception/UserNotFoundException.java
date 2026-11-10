package com.sprint.mission.discodeit.exception;

import java.util.List;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("유저를 찾을 수 없음");
    }
}
