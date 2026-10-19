package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;


    private String channelName;
    private String topic;
    private String type;
    private List<UUID> participantIds;


    public Channel(String ChannelName, String Topic, String Type) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.channelName = ChannelName;
        this.topic = Topic;
        this.type = Type;
        this.participantIds = new ArrayList<>();
    }
    public UUID getId() {
        return id;
    }
    public Long getCreatedAt() {
        return createdAt;
    }
    public Long getUpdatedAt() {
        return updatedAt;
    }
    public String getChannelName() {
        return channelName;
    }
    public String getTopic() {
        return topic;
    }
    public String getType() {
        return type;
    }
    public List<UUID> getParticipantIds() {
        return participantIds;
    }
    public void update(String channelName, String topic, String type) {
        this.channelName = channelName;
        this.topic = topic;
        this.type = type;
        this.updatedAt = System.currentTimeMillis();
    }
    public void addParticipant(UUID userId) {
        this.participantIds.add(userId);
        this.updatedAt = System.currentTimeMillis();
    }
    public void removeParticipant(UUID userId) {
        this.participantIds.remove(userId);
        this.updatedAt = System.currentTimeMillis();
    }


}
