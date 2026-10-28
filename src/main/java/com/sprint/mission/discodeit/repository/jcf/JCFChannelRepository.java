package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data;

    public JCFChannelRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {

        if(existsByChannelId(channel.getId())) {
            // 같은 ID를 가진 채널의 인덱스를 찾는다.
            // 해당 채널을 찾지 못하면 예외를 발생시킨다.
            int index = IntStream.range(0, data.size())
                    .filter(i -> data.get(i).getId().equals(channel.getId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("수정할 채널을 찾을 수 없습니다."));

            data.set(index, channel); // 기존 채널 객체를 새 채널 객체로 교체한다. (수정)
        } else {
            data.add(channel); // 새로운 채널을 목록에 추가한다. (생성)
        }

        return channel; // 저장하거나 수정한 채널을 반환한다.
    }

    @Override
    public Optional<Channel> findByChannelId(UUID id) {
        // ID가 일치하는 채널을 찾아 반환한다. (단건 조회)
        // 일치하는 채널이 없으면 Optional.empty()를 반환한다.
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        // data에 저장된 채널들을 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data);
    }

    @Override
    public boolean existsByChannelId(UUID id) {
        // 해당 채널의 ID가 존재하는지 확인한다.
        return data.stream().anyMatch(channel -> channel.getId().equals(id));
    }

    @Override
    public void deleteByChannelId(UUID id) {
        // ID가 일치하는 채널을 삭제한다. (삭제)
        boolean remove = data.removeIf(channel -> channel.getId().equals(id));

        // 삭제된 채널이 없으면 예외를 발생시킨다.
        if(!remove){
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다");
        }
    }
}