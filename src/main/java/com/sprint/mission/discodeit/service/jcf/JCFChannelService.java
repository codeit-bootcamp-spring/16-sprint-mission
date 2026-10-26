package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    // 생성자: 채널을 저장할 공간 만들기
    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    // 채널 생성
    @Override
    public Channel create(Channel channel) {

        if (channel == null) {
            throw new IllegalArgumentException("채널은 null일 수 없습니다.");
        }

        if (channel.getChannel() == null) {
            throw new IllegalArgumentException("채널 이름은 null일 수 없습니다.");
        }

        // 이미 같은 이름의 채널이 있는지 확인
        for (Channel existingChannel : data.values()) {
            if (existingChannel.getChannel().equals(channel.getChannel())) {
                throw new IllegalArgumentException("이미 존재하는 채널 이름입니다.");
            }
        }

        // 중복이 없으면 저장
        data.put(channel.getId(), channel);
        return channel;
    }

    // ID로 채널 조회
    @Override
    public Channel findById(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        return data.get(id);
    }

    // 모든 채널 조회
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    // 채널 수정
    @Override
    public Channel update(UUID id, Channel channel) {

        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        if (channel == null) {
            throw new IllegalArgumentException("채널은 null일 수 없습니다.");
        }

        if (channel.getChannel() == null) {
            throw new IllegalArgumentException("채널 이름은 null일 수 없습니다.");
        }

        // 수정할 채널이 존재하는지 확인
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("수정할 채널이 존재하지 않습니다.");
        }

        // 다른 채널과 이름이 중복되는지 확인
        for (Channel existingChannel : data.values()) {
            if (!existingChannel.getId().equals(id)
                    && existingChannel.getChannel().equals(channel.getChannel())) {
                throw new IllegalArgumentException("이미 존재하는 채널 이름입니다.");
            }
        }

        // 기존 채널 객체를 ID로 찾기
        Channel existingChannel = data.get(id);

        // 기존 채널의 이름만 수정하고 수정 시간을 갱신
        existingChannel.update(channel.getChannel());

        // 수정된 기존 채널 객체 반환
        return existingChannel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("삭제할 채널이 존재하지 않습니다.");
        }

        data.remove(id);
    }
}
