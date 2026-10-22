
package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    // 채널 데이터를 저장할 파일
    private final Path filePath = Paths.get("data", "channels.dat");

    // 채널 데이터를 메모리에서 관리
    private final Map<UUID, Channel> data;

    public FileChannelService() {
        this.data = loadData();
    }

    // 파일에서 채널 데이터 불러오기
    private Map<UUID, Channel> loadData() {
        try {
            if (!Files.exists(filePath)) {
                return new HashMap<>();
            }

            try (ObjectInputStream input = new ObjectInputStream(
                    Files.newInputStream(filePath))) {
                return (Map<UUID, Channel>) input.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("채널 데이터를 불러오지 못했습니다.", e);
        }
    }

    // 채널 데이터를 파일에 저장
    private void saveData() {
        try {
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream output = new ObjectOutputStream(
                    Files.newOutputStream(filePath))) {
                output.writeObject(data);
            }

        } catch (IOException e) {
            throw new IllegalStateException("채널 데이터를 저장하지 못했습니다.", e);
        }
    }

    // 채널 생성
    @Override
    public Channel create(Channel channel) {
        if (channel == null || channel.getChannel() == null) {
            throw new IllegalArgumentException("채널 정보는 null일 수 없습니다.");
        }

        // 같은 이름의 채널이 있는지 확인
        for (Channel existing : data.values()) {
            if (existing.getChannel().equals(channel.getChannel())) {
                throw new IllegalArgumentException("이미 존재하는 채널 이름입니다.");
            }
        }

        data.put(channel.getId(), channel);
        saveData();

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
        if (id == null || channel == null || channel.getChannel() == null) {
            throw new IllegalArgumentException("수정할 채널 정보가 올바르지 않습니다.");
        }

        Channel existingChannel = data.get(id);

        if (existingChannel == null) {
            throw new IllegalArgumentException("수정할 채널이 존재하지 않습니다.");
        }

        // 현재 채널 자신을 제외하고 중복 이름 검사
        for (Channel other : data.values()) {
            if (!other.getId().equals(id)
                    && other.getChannel().equals(channel.getChannel())) {
                throw new IllegalArgumentException("이미 존재하는 채널 이름입니다.");
            }
        }

        existingChannel.update(channel.getChannel());
        saveData();

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
        saveData();
    }
}
