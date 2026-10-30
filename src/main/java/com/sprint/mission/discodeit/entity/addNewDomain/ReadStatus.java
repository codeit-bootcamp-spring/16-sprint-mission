package com.sprint.mission.discodeit.entity.addNewDomain;

import com.sprint.mission.discodeit.entity.BaseEntity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter // Lombok을 통해 모든 필드의 Getter 메서드를 자동 생성한다.
public class ReadStatus extends BaseEntity {

    private final UUID userId; // 사용자 식별자
    private final UUID channelId; // 채널 식별자
    private Instant lastReadAt; // 마지막으로 읽은 시각

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    // 마지막으로 읽은 시각을 갱신합니다.
    public void updateLastReadAt(Instant lastReadAt){
        this.lastReadAt = lastReadAt;
        touch();
    }
}