package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        // 채널 ID를 키로 사용하여 저장하거나 수정한다.
        data.put(channel.getId(), channel);

        // 저장하거나 수정한 채널을 반환한다.
        return channel;
    }

    @Override
    public Optional<Channel> findByChannelId(UUID id) {
        // ID가 일치하는 채널을 찾아 반환한다. (단건 조회)
        // 일치하는 채널이 없으면 Optional.empty()를 반환한다.
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        // data에 저장된 채널들을 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsByChannelId(UUID id) {
        // 해당 채널의 ID가 존재하는지 확인한다.
        return data.containsKey(id);
    }

    @Override
    public void deleteByChannelId(UUID id) {
        // ID가 일치하는 채널을 삭제한다. (삭제)
        Channel removed = data.remove(id);

        // 삭제된 채널이 없으면 예외를 발생시킨다.
        if(removed == null){
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다");
        }
    }
}