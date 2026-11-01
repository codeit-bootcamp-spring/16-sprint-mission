package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String content;
    private final UUID channelId;
    private final UUID authorId;
    // 참조 변수에 할당 된 final은 참조 변수의 재할당을 금지하는 것이지, 참조 하고 있는 객체의 변경을 막는 것은 아닙니다.
    // 따라서, final을 붙여도 attachmentIds.add()나 attachmentIds.remove()는 정상적으로 사용할 수 있습니다.
    // 단, 실제 리스트가 수정 가능한 구현체여야합니다.
    private final List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.content = content;
        //
        this.channelId = channelId;
        this.authorId = authorId;
        // attachmentIds는 수정 가능한 ArrayList를 사용해야합니다,
        // 필드나 생성자의 인자에 ArrayList를 명시하는 것보다,
        // 생성자의 인자에 들어올 타입을 알려주는 것이 더 좋습니다
        this.attachmentIds = attachmentIds == null
                ? new ArrayList<UUID>()
                : new ArrayList<UUID>(attachmentIds);
    }

    public void update(String newContent, List<BinaryContent> newBinaryContents) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        /*
        if (newBinaryContent != null && !this.attachmentIds.contains(newBinaryContent.getId())) {
            this.attachmentIds.add(newBinaryContent.getId());
            anyValueUpdated = true;
        }
         */

        List<UUID> newAttachmentIds = newBinaryContents.stream()
                .filter(b -> b != null && !this.attachmentIds.contains(b.getId()))
                .map(BinaryContent::getId)
                .distinct()
                .toList();

        if (this.attachmentIds.addAll(newAttachmentIds)) { // newAttachmentIds는 수정 불가여도됩니다.

            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
