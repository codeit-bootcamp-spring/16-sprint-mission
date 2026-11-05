package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper { // 복잡할 때 빈으로 만든다, 1 - 2 개 일 때는 user 엔티티안에 Mapper를 만든다.
    // BasicUserService basicUserService;
    /*
    public static UserResponse toResponse(User user, boolean isOnline) {
        return new UserResponse(
                user.getId(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                isOnline
        );
    }
     */
    // JPA ORM를 쓰면 user를 가져오면 userStatus 도 가져온다,
    // public UserResponse toResponse(User user, UserStatus userStatus) { // body };
    public UserResponse toResponse(User user, UserStatus userStatus) {
        return new UserResponse(
                user.getId(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                userStatus.isOnline()
        );
    }
}
