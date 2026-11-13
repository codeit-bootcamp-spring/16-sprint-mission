package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class BaseEntity implements Serializable {
  private final UUID id;
  private final Instant createdAt;

  @Serial private static final long serialVersionUID = 1L;

  protected BaseEntity() {
    id = UUID.randomUUID();
    createdAt = Instant.now();
  }
}
