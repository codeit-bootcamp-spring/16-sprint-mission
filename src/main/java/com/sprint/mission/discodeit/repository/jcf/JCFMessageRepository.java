package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository{
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        // 메시지 ID를 키로 사용하여 저장하거나 수정한다.
        data.put(message.getId(), message);

        // 저장하거나 수정한 메시지 내용을 반환한다.
        return message;
    }

    @Override
    public Optional<Message> findByMessageId(UUID id) {
        // ID가 일치하는 메시지를 찾아 반환한다. (단건 조회)
        // 일치하는 메시지가 없으면 Optional.empty()를 반환한다.
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        // data에 저장된 메시지를 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsByMessageId(UUID id) {
        // 해당 메시지의 ID가 존재하는지 확인한다. (존재 여부)
        return data.containsKey(id);
    }

    @Override
    public void deleteByMessageId(UUID id) {
        // ID가 일치하는 메시지를 삭제한다. (삭제)
        Message removed = data.remove(id);

        // 삭제된 메시지가 없으면 예외를 발생시킨다.
        if(removed == null){
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }
    }
}