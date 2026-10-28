package discodeit.repository;

import discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);          // 저장 (생성 + 수정 겸용)
    Optional<Channel> findById(UUID channelId);    // 단건 조회
    List<Channel> findAll();                // 전체 조회
    void deleteById(UUID id);
}
