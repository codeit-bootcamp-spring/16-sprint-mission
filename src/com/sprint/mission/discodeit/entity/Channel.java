package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {

    private String name;
    private String description;
    private List<UUID> memberIds = new ArrayList<>();

    public Channel(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        updateUpdatedAt();
    }

    public List<UUID> getMemberIds() {
        return List.copyOf(memberIds);
    }

    public void addMember(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
    }

    public void removeMember(UUID userId) {
        memberIds.remove(userId);
    }

    public boolean hasMember(UUID userId) {
        return memberIds.contains(userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Channel channel = (Channel) obj;
        return getId().equals(channel.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}