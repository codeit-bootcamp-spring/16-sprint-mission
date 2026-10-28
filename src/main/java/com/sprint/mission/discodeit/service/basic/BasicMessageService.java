package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private User requireUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private Channel requireChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    private Message requireMessage(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
    }

    private void validateParticipant(UUID userId, Channel channel, String action) {
        if (!channel.getParticipantIds().contains(userId)) {
            throw new IllegalArgumentException("채널에 참여한 유저만 메시지를 " + action + "할 수 있습니다.");
        }
    }

    private void validateSender(Message message, UUID userId, String action) {
        if (!message.getSenderId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 메시지만 " + action + "할 수 있습니다.");
        }
    }

    @Override
    public Message createMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메시지가 null입니다.");
        }

        if (messageRepository.findById(message.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 메시지입니다.");
        }

        User user = requireUser(message.getSenderId());
        Channel channel = requireChannel(message.getChannelId());
        validateParticipant(user.getId(), channel, "작성");

        user.addMessageId(message.getId());
        channel.addMessageId(message.getId());

        userRepository.save(user);
        channelRepository.save(channel);
        return messageRepository.save(message);
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return requireMessage(messageId);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        requireUser(userId);
        return messageRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed()).toList();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        requireChannel(channelId);
        return messageRepository.findByChannelId(channelId).stream()
                .sorted(Comparator.comparing(Message::getCreatedAt)).toList();
    }

    @Override
    public List<Message> getMessagesByUserAndChannel(UUID userId, UUID channelId) {
        requireUser(userId);
        requireChannel(channelId);
        return messageRepository.findByUserIdAndChannelId(userId, channelId).stream()
                .sorted(Comparator.comparing(Message::getCreatedAt)).toList();
    }

    @Override
    public Message updateMessage(UUID messageId, UUID userId, String content) {
        Message message = requireMessage(messageId);
        validateSender(message, userId, "수정");

        requireUser(message.getSenderId());
        Channel channel = requireChannel(message.getChannelId());
        validateParticipant(userId, channel, "수정");

        message.updateContent(content);
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(UUID messageId, UUID userId) {
        Message message = requireMessage(messageId);
        validateSender(message, userId, "삭제");

        User user = requireUser(message.getSenderId());
        Channel channel = requireChannel(message.getChannelId());
        validateParticipant(userId, channel, "삭제");

        removeMessage(message, user, channel);
    }

    private void removeMessage(Message message, User user, Channel channel) {
        user.removeMessageId(message.getId());
        channel.removeMessageId(message.getId());

        userRepository.save(user);
        channelRepository.save(channel);
        messageRepository.deleteById(message.getId());
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        Channel channel = requireChannel(channelId);

        for (Message message : messageRepository.findByChannelId(channelId)) {
            userRepository.findById(message.getSenderId())
                    .ifPresentOrElse(
                            user -> removeMessage(message, user, channel),
                            () -> {
                                channel.removeMessageId(message.getId());
                                channelRepository.save(channel);
                                messageRepository.deleteById(message.getId());
                            }
                    );
        }
    }

    @Override
    public void deleteMessagesByUserId(UUID userId) {
        User user = requireUser(userId);

        for (Message message : messageRepository.findByUserId(userId)) {
            channelRepository.findById(message.getChannelId())
                    .ifPresent(channel -> {
                        channel.removeMessageId(message.getId());
                        channelRepository.save(channel);
                    });

            user.removeMessageId(message.getId());
            userRepository.save(user);
            messageRepository.deleteById(message.getId());
        }
    }
}
