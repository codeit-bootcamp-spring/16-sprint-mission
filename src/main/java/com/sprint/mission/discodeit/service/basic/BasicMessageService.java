package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository,
                               UserRepository userRepository,
                               ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(UUID sentFromId, String content, UUID channelId) {
        // 유저와 채널 존재 여부 확인
        User sentFrom = this.userRepository.findById(sentFromId)
                .orElseThrow(() -> new NoSuchElementException("메세지를 보내는 유저가 존재하지 않습니다."));
        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("메세지를 보낼 채널이 존재하지 않습니다."));
        sentFrom.validateNotDeleted();
        channel.validateNotDeleted();

        // 채널 가입 여부 확인
        channel.getUsers()
                .stream()
                .filter(userId -> this.userRepository.findById(userId).isPresent())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("채널에 가입되지 않은 유저입니다."));

        Message message = this.messageRepository.create(sentFromId, content, channelId);

        // 메세지 추가 시 유저와 채널에도 추가
        sentFrom.addMessage(message.getId());
        this.userRepository.update(sentFrom.getId(), sentFrom);

        channel.addMessage(message.getId());
        this.channelRepository.update(channel.getId(), channel);

        return message;
    }

    @Override
    public Message update(UUID id, Message newMessage) {
        Message message = this.messageRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다."));
        // 갱신할 메세지 정보의 상태가 활동 중이면 현재 메세지도 검사
        if (!newMessage.isDeleted()) {
            message.validateNotDeleted();
        }

        message.update(newMessage);
        return this.messageRepository.update(id, message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return this.messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return this.messageRepository.findAll();
    }

    public Message updateMessage(UUID id, String content) {
        Message message = this.messageRepository.findById(id).orElseThrow(() -> new NoSuchElementException("수정할 메세지가 존재하지 " +
                "않습니다."));
        message.validateNotDeleted();
        message.setContent(content);
        return this.messageRepository.update(id, message);
    }

    @Override
    public void delete(UUID id) {
        Message message = this.messageRepository.findById(id).orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 존재하지 " +
                "않습니다."));
        message.validateNotDeleted();

        // 메세지 삭제 시 채널과 유저에도 메세지 삭제
        Channel channel =
                this.channelRepository.findById(message.getChannelId()).orElseThrow(
                        () -> new NoSuchElementException("메세지를 삭제할 채널이 없습니다."));

        channel.removeMessage(id);
        this.channelRepository.update(channel.getId(), channel);

        User user =
                this.userRepository.findById(message.getSentFromId()).orElseThrow(
                        () -> new NoSuchElementException("메세지를 삭제할 채널이 없습니다."));
        user.removeMessage(id);
        this.userRepository.update(user.getId(), user);

        this.messageRepository.delete(id);
    }
}
