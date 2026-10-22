
package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileMessageService implements MessageService {

    // 메시지 데이터를 저장할 파일
    private final Path filePath = Paths.get("data", "messages.dat");

    // 메시지 데이터를 메모리에서 관리
    private final Map<UUID, Message> data;

    // 사용자와 채널을 확인하기 위한 서비스
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자: 필요한 서비스들을 전달받고 저장된 메시지 불러오기
    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        this.data = loadData();
    }

    // 파일에서 메시지 데이터 불러오기
    private Map<UUID, Message> loadData() {
        try {
            if (!Files.exists(filePath)) {
                return new HashMap<>();
            }

            try (ObjectInputStream input = new ObjectInputStream(
                    Files.newInputStream(filePath))) {
                return (Map<UUID, Message>) input.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("메시지 데이터를 불러오지 못했습니다.", e);
        }
    }

    // 메시지 데이터를 파일에 저장
    private void saveData() {
        try {
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream output = new ObjectOutputStream(
                    Files.newOutputStream(filePath))) {
                output.writeObject(data);
            }

        } catch (IOException e) {
            throw new IllegalStateException("메시지 데이터를 저장하지 못했습니다.", e);
        }
    }

    // 메시지 생성
    @Override
    public Message create(Message message) {
        if (message == null || message.getContent() == null
                || message.getUser() == null || message.getChannel() == null) {
            throw new IllegalArgumentException("메시지 정보가 올바르지 않습니다.");
        }

        // 작성자가 실제로 존재하는지 확인
        if (userService.findById(message.getUser().getId()) == null) {
            throw new IllegalArgumentException("작성자가 존재하지 않습니다.");
        }

        // 채널이 실제로 존재하는지 확인
        if (channelService.findById(message.getChannel().getId()) == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }

        data.put(message.getId(), message);
        saveData();

        return message;
    }

    // ID로 메시지 조회
    @Override
    public Message findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        return data.get(id);
    }

    // 모든 메시지 조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    // 메시지 수정
    @Override
    public Message update(UUID id, Message message) {
        if (id == null || message == null || message.getContent() == null) {
            throw new IllegalArgumentException("수정할 메시지 정보가 올바르지 않습니다.");
        }

        Message existingMessage = data.get(id);

        if (existingMessage == null) {
            throw new IllegalArgumentException("수정할 메시지가 존재하지 않습니다.");
        }

        // 메시지 내용만 수정하고 기존 작성자와 채널은 유지
        existingMessage.update(message.getContent());
        saveData();

        return existingMessage;
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("삭제할 메시지가 존재하지 않습니다.");
        }

        data.remove(id);
        saveData();
    }
}

