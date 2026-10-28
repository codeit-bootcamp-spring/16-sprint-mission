package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository messageRepository;

    @Override
    public Message create(Message message) {
        userService.findById(message.getAuthorId());
        Channel channel = channelService.findById(message.getChannelId());
        if (!channel.hasUser(message.getAuthorId())) {                      // 채널 멤버인지 검증 /
            throw new IllegalArgumentException("채널 멤버만 메시지를 작성할 수 있습니다.");
        }
        Message saved = messageRepository.save(message);
        userService.addMessage(message.getAuthorId(), message.getId());
        channelService.addMessage(message.getChannelId(), message.getId());
        return saved;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public Message update(UUID id, UUID requesterId, String content) {
        Message message = findById(id);
        requireSender(message, requesterId, "메시지 수정 권한이 없습니다.");
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Message message = findById(id);
        requireSender(message, requesterId, "메시지 삭제 권한이 없습니다.");

        userService.removeMessage(message.getAuthorId(), id);
        channelService.removeMessage(message.getChannelId(), id);
        messageRepository.deleteById(id);

    }

    private void requireSender(Message message, UUID requesterId, String errorMessage) {
        if(!message.getAuthorId().equals(requesterId)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
