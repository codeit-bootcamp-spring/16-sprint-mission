package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity {

    private String name;
    private String email;
    private List<UUID> channelIds = new ArrayList<>();

    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
        updateUpdatedAt();
    }

    public List<UUID> getChannelIds() {
        return List.copyOf(channelIds);
    }

    public void addChannel(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        if (!channelIds.contains(channelId)) {
            channelIds.add(channelId);
        }
    }

    public void removeChannel(UUID channelId) {
        channelIds.remove(channelId);
    }

    public boolean hasChannel(UUID channelId) {
        return channelIds.contains(channelId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        User user = (User) obj;
        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}