package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageData = new LinkedHashMap<>();
    private UserService userService;
    private ChannelService channelService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    private Message requireMessage(UUID id) {
        Message message = messageData.get(id);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }
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
        if (message == null) {
            throw new IllegalArgumentException("메시지가 null입니다.");
        }
        if (messageData.containsKey(message.getId())) {
            throw new IllegalArgumentException("이미 존재하는 메시지입니다.");
        }
        User user = userService.getUserById(message.getSenderId());
        Channel channel = channelService.getChannelById(message.getChannelId());
        validateParticipant(user.getId(), channel, "작성");
        user.addMessageId(message.getId());
        channel.addMessageId(message.getId());
        messageData.put(message.getId(), message);
        return message;
    }

    @Override
    public Message getMessageById(UUID id) {
        return requireMessage(id);
    }

    @Override
    public List<Message> getAllMessages() {
        return List.copyOf(messageData.values());
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        userService.getUserById(userId);
        return messageData.values().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        channelService.getChannelById(channelId);
        return messageData.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> getMessagesByUserAndChannel(UUID userId, UUID channelId) {
        userService.getUserById(userId);
        channelService.getChannelById(channelId);
        return messageData.values().stream()
                .filter(message -> message.getSenderId().equals(userId)
                        && message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public Message updateMessage(UUID messageId, UUID userId, String content) {
        Message message = requireMessage(messageId);
        validateSender(message, userId, "수정");
        Channel channel = channelService.getChannelById(message.getChannelId());
        validateParticipant(userId, channel, "수정");
        message.updateMessage(content);
        return message;
    }

    private void removeMessage(Message message, User user, Channel channel) {
        user.removeMessageId(message.getId());
        channel.removeMessageId(message.getId());
        messageData.remove(message.getId());
    }

    @Override
    public void deleteMessage(UUID messageId, UUID userId) {
        Message message = requireMessage(messageId);
        validateSender(message, userId, "삭제");
        User user = userService.getUserById(message.getSenderId());
        Channel channel = channelService.getChannelById(message.getChannelId());
        validateParticipant(userId, channel, "삭제");
        removeMessage(message, user, channel);
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        Channel channel = channelService.getChannelById(channelId);
        for (Message message : getMessagesByChannelId(channelId)) {
            User user = userService.getUserById(message.getSenderId());
            removeMessage(message, user, channel);
        }
    }

    @Override
    public void deleteMessagesByUserId(UUID userId) {
        User user = userService.getUserById(userId);
        for (Message message : getMessagesByUserId(userId)) {
            Channel channel = channelService.getChannelById(message.getChannelId());
            removeMessage(message, user, channel);
        }
    }
}
