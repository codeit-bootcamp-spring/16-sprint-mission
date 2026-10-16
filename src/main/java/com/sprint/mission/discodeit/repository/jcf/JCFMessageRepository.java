package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public JCFMessageRepository(UserRepository userService, ChannelRepository channelService) {
        this.data = new HashMap<>();
        this.userRepository = userService;
        this.channelRepository = channelService;
    }

    @Override
    public Message create(Message message) {
        if (userRepository.findById(message.getUser().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 User는 Message를 작성할 수 없습니다.");
        }
        if (channelRepository.findById(message.getChannel().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 Channel에는 Message를 작성할 수 없습니다.");
        }
        data.put(message.getId(), message);

        // 작성자와 Channel이 보관하는 Message 목록에도 반영한다.
        userRepository.addMessage(message);
        channelRepository.addMessage(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, String newMessage) {
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
        message.update(newMessage);

        userRepository.refreshMessage(message);
        channelRepository.refreshMessage(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }

        // User/Channel이 보관 중인 Message 목록에서도 제거한다.
        userRepository.removeMessage(id);
        channelRepository.removeMessage(id);
    }
}
