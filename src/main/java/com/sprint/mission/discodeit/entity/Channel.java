package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;

@Getter
public class Channel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private String name;
    private String description;
    private final ChannelType type;

    public Channel(
            ChannelType type,
            String name,
            String description
    ) {
        super();

        Validation.validateNull(type, "type");

        if (type == ChannelType.PUBLIC) {
            Validation.validateBlank(name, "name");
        } else {
            name = null;
            description = null;
        }

        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void changeName(String name) {
        Validation.validateBlank(name, "name");

        this.name = name;

        super.update();
    }

    public void changeDescription(String description) {
        this.description = description;

        super.update();
    }
}
