package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.util.*;
import lombok.Getter;

@Getter
public class Channel extends BaseUpdatableEntity {
  private ChannelType type;
  private String name;
  private String description;
  private final Set<UUID> users = new HashSet<>();
  private final List<UUID> messages = new ArrayList<>();

  @Serial private static final long serialVersionUID = 1L;

  public Channel(ChannelType type, String name, String description) {
    super();

    if (type == ChannelType.PUBLIC) validateName(name);
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public Set<UUID> getUsers() {
    return Collections.unmodifiableSet(users);
  }

  public List<UUID> getMessages() {
    return Collections.unmodifiableList(messages);
  }

  public boolean hasUser(UUID userId) {
    return users.contains(userId);
  }

  public void update(String name, String description) {
    if (name != null) {
      validateName(name);
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
    super.touch();
  }

  public void addUser(UUID userId) {
    if (!users.add(userId)) {
      throw new IllegalArgumentException("이미 가입된 채널입니다.");
    }
    touch();
  }

  public void removeUser(UUID userId) {
    if (!users.remove(userId)) {
      throw new IllegalArgumentException("채널 멤버가 아닙니다.");
    }
    touch();
  }

  public void addMessage(UUID messageId) {
    messages.add(messageId);
    touch();
  }

  public void removeMessage(UUID messageId) {
    messages.remove(messageId);
    touch();
  }

  @Override
  public String toString() {
    return "Channel{"
        + ", type="
        + type
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", users="
        + users
        + ", messages="
        + messages
        + '}';
  }

  private static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("채널명은 필수입니다.");
    }
  }
}
