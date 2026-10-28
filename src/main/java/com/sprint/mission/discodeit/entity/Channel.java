package com.sprint.mission.discodeit.entity;

//사용자들이 메시지를 주고받는 채널 정보

public class Channel extends BaseEntity{
    private static final long serialVersionUID = 1L;

    // Channel field
    private final ChannelType type;
    private String name;
    private String description;

    /** 기존 코드와의 호환을 위해 타입을 생략하면 공개 채널로 만듭니다. */
    public Channel(String name, String description) {
        this(ChannelType.PUBLIC, name, description);
    }

    public Channel(ChannelType type, String name, String description) {
        super();
        if (type == null) {
            throw new IllegalArgumentException("Channel type cannot be null.");
        }
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public ChannelType getType() {
        // ChannelType 필드가 추가되기 전 생성된 직렬화 파일에는 이 필드가 없으므로 PUBLIC으로 해석합니다.
        return type == null ? ChannelType.PUBLIC : type;
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
}

