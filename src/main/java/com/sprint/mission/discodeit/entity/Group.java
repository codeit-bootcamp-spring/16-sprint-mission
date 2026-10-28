package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.Serializable;

public class Group extends BaseEntity implements Serializable {
    private UUID ownerId;
    private String name;
    private String type;
    private List<UUID> itemIds;
    private static final long serialVersionUID = 1L;


    public Group(UUID ownerId, String name, String type) {
        super();
        this.ownerId = ownerId;
        this.name = name;
        this.type = type;
        this.itemIds = new ArrayList<>();
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
        return new ArrayList<>(itemIds);
    }
public void update(String name) {
        this.name = name;
    updateTimestamp();
}
public void addItem(UUID itemId) {
        this.itemIds.add(itemId);
    updateTimestamp();
}
public void removeItem(UUID itemId) {
        this.itemIds.remove(itemId);
    updateTimestamp();
}
}