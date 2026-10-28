package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** MessageRepository를 Java Collection Framework의 Map으로 구현합니다. */
public class JCFMessageRepository implements MessageRepository {

    // Message ID를 키로, Message 객체를 값으로 보관합니다.
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public void save(Message message) {
        // 같은 ID가 있으면 덮어씁니다.
        // 중복 등록을 허용할지는 서비스가 먼저 검사합니다.
        data.put(message.getId(), message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        // 저장된 메시지가 없으면 Optional.empty()를 돌려줍니다.
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        // Map의 내부 값 목록을 외부에서 수정하지 못하게 복사해 반환합니다.
        return List.copyOf(data.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        // 실제로 제거된 메시지가 있으면 true, 없으면 false입니다.
        return data.remove(id) != null;
    }
}
