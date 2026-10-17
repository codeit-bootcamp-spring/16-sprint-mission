package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private UUID ownerId;
    private String name;
    private String type;
    private List<UUID> itemIds;


    public Group(UUID ownerId, String name, String type) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.ownerId = ownerId;
        this.name = name;
        this.type = type;
        this.itemIds = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }
    public Long getCreatedAt() {
        return createdAt;
    }
    public  Long getUpdatedAt() {
        return updatedAt;
    }
    public  UUID getOwnerId() {
        return ownerId;
    }
    public  String getName() {
        return name;
    }
    public  String getType() {
        return type;
    }
    public  List<UUID> getItemIds() {
        return itemIds;
    }
public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
}
public void addItem(UUID itemId) {
        this.itemIds.add(itemId);
        this.updatedAt = System.currentTimeMillis();
}
public void removeItem(UUID itemId) {
        this.itemIds.remove(itemId);
        this.updatedAt = System.currentTimeMillis();
}
}