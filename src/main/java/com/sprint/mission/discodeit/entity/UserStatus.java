package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        /* 엔티티의 검증 로직은 일단 사용하지 않습니다.
        // UUID는 isBlank() 메서드가 존재하지 않고,
        // UUID는 길이가 정해져있기 때문에 공백 여부를 검증할 필요성이없습니다.
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
         */
        this.id =  UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // 이 UserStatus 엔티티에서 update()를 호출함으로, 메서드 인자에 UUID userId를 받아서
    // 해당 userID가 이 엔티티의 userId와 같은 지 검증할 필요가 업습니다.
    // 이미 이 엔티티에서 메서드를 호출한다는것은,
    // 즉, UserStatus 객체에서 update()를 호출한다는 것은 이미 갱신할 UserStatus 객체를 확보했다는 의미입니다.
    public void update() {
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        // duration을 계산하는 메서드는 굳이 따로 만들 필요가 없습니다.
        // 로직이 단순하고, 재사용할 일이 없습니다.
        Duration duration = Duration.between(this.updatedAt, Instant.now());

        boolean isOnline = !duration.isNegative()
                && duration.compareTo(Duration.ofMinutes(5)) <= 0;

        return isOnline;
    }
}
