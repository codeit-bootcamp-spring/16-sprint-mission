package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatus extends Base {
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public void update(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        updateUpdatedAt();
    }
}
