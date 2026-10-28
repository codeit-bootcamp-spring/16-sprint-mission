package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity{
        private UUID userId;
        private Instant lastActiveAt;

        public UserStatus(UUID userId, Instant lastActiveAt) {
                this.userId = userId;
                this.lastActiveAt = lastActiveAt;
        }

        public boolean isOnline() {
                Instant now = Instant.now();
            return !lastActiveAt.isBefore(now.minusSeconds(300)) && !lastActiveAt.isAfter(now);
        }
        public void update(Instant lastActiveAt) {
                this.lastActiveAt = lastActiveAt;
                updateTime();
        }
}
