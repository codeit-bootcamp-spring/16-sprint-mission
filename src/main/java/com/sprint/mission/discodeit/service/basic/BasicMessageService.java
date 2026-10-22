package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Validation;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicMessageService(UserRepository userRepository,
                               ChannelRepository channelRepository,
                               MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    private User requireUser(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        return user;
    }

    private Channel requireChannel(UUID id) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        return channel;
    }

    private Message requireMessage(UUID id) {
        Message message = messageRepository.findById(id);
        if (message == null) throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        return message;
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
        if (message == null) throw new IllegalArgumentException("메시지가 null입니다.");
        if (messageRepository.findById(message.getId()) != null) {
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
    public Message getMessageById(UUID messageId) { return requireMessage(messageId); }

    @Override
    public List<Message> getAllMessages() { return messageRepository.findAll(); }

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
        User user = requireUser(message.getSenderId());
        Channel channel = requireChannel(message.getChannelId());
        validateParticipant(userId, channel, "수정");

        // 잘못된 내용이면 관계 ID를 변경하기 전에 실패시킨다.
        Validation.validateBlank(content, "message");
        message.updateMessage(content);
        userRepository.save(user);
        channelRepository.save(channel);
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
            User user = userRepository.findById(message.getSenderId());
            if (user != null) removeMessage(message, user, channel);
            else {
                channel.removeMessageId(message.getId());
                channelRepository.save(channel);
                messageRepository.deleteById(message.getId());
            }
        }
    }

    @Override
    public void deleteMessagesByUserId(UUID userId) {
        User user = requireUser(userId);
        for (Message message : messageRepository.findByUserId(userId)) {
            Channel channel = channelRepository.findById(message.getChannelId());
            user.removeMessageId(message.getId());
            if (channel != null) {
                channel.removeMessageId(message.getId());
                channelRepository.save(channel);
            }
            userRepository.save(user);
            messageRepository.deleteById(message.getId());
        }
    }
}
