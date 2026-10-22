package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class Channel extends BaseEntity implements Serializable {


    private String channelName;
    private String topic;
    private String type;
    private List<UUID> participantIds;
    private static final long serialVersionUID = 1L;


    public Channel(String ChannelName, String Topic, String Type) {
        super();
        this.channelName = ChannelName;
        this.topic = Topic;
        this.type = Type;
        this.participantIds = new ArrayList<>();
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
        return new ArrayList<>(participantIds);
    }
    public void update(String channelName, String topic, String type) {
        this.channelName = channelName;
        this.topic = topic;
        this.type = type;
        updateTimestamp();
    }
    public void addParticipant(UUID userId) {
        this.participantIds.add(userId);
        updateTimestamp();
    }
    public void removeParticipant(UUID userId) {
        this.participantIds.remove(userId);
        updateTimestamp();
    }


}
