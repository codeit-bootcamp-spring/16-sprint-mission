package com.sprint.mission.discodeit.entity.addNewDomain;

import com.sprint.mission.discodeit.entity.BaseEntity;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter // Lombok을 통해 모든 필드의 Getter 메서드를 자동 생성한다.
public class UserStatus extends BaseEntity {

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        super();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    // 마지막 접속 시간을 갱신합니다.
    public void updateLastActiveAt(Instant lastActiveAt){
        this.lastActiveAt = lastActiveAt;
        touch();
    }

    // 온라인 상태을 판단합니다.
    public boolean isOnline(){
        return !lastActiveAt.isBefore(Instant.now().minus(Duration.ofMinutes(5)));
    }
}