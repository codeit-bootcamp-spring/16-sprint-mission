package discodeit.repository;

import discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);          // 저장 (생성 + 수정 겸용)
    Optional<Message> findById(UUID messageId);    // 단건 조회
    List<Message> findAll();                // 전체 조회
    void deleteById(UUID messageId);
    public void deleteByUserId(UUID userId);
    public void deleteByChannelId(UUID channelId);
}
