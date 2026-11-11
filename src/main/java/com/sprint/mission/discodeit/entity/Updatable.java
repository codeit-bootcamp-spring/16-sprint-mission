package com.sprint.mission.discodeit.entity;

import java.time.Instant;

public interface Updatable {
    Instant getUpdatedAt();

    void update();
}
