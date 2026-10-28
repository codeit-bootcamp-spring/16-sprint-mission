package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** ChannelRepository를 Java Collection Framework의 Map으로 구현합니다. */
public class JCFChannelRepository implements ChannelRepository {

    // 메모리 안에서 UUID로 객체를 빠르게 찾습니다. 프로그램 종료 후 데이터는 사라집니다.
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public boolean existsById(UUID id) {
        // Map의 키 존재 여부만 확인하므로 객체 전체를 꺼낼 필요가 없습니다.
        return data.containsKey(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        return data.remove(id) != null;
    }
}
