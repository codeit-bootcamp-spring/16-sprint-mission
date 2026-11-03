package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public JCFMessageService(UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message save(Message message) {
        if (message == null || message.getId() == null) {
            throw new NullPointerException("데이터 오류");
        }
        if (message.getUserId() == null || message.getChannelId() == null) {
            throw new NullPointerException("ID 데이터 누락");
        }

        if (userRepository.findId(message.getUserId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (channelRepository.findId(message.getChannelId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        data.put(message.getId(), message);
        return message;
    }
    @Override
    public List<Message> findId(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            return Collections.emptyList();
        }
        List<Message> result = new ArrayList<>();
        result.add(message);
        return result;
    }

    @Override
    public List<Message> findUserlId(UUID userId) {
        return data.values().stream()
                .filter(message -> message.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message->message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = data.get(id);
        if (message == null) {
            throw new NullPointerException("해당메시지 없음");
        }
        message.setContent(content);
        return message;
    }

    @Override
    public void deleteId(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values()
                .removeIf(message -> message.getChannelId().equals(channelId));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.values().
                removeIf(message -> message.getUserId().equals(userId));
    }
}
