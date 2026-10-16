package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageData;

    private UserService userService;
    private ChannelService channelService;

    public JCFMessageService() {
        this.messageData = new LinkedHashMap<>();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    // 참여 검증
    private void validateParticipant(User user, Channel channel, String action) {
        if (!channelService.isParticipant(channel.getId(), user.getId())) {
            throw new IllegalArgumentException("채널에 참여한 유저만 메시지를 " + action + "할 수 있습니다.");
        }
    }

    // 작성자가 본인인지 검증
    private void validateSender(Message message, UUID userId, String action) {
        if (!message.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 메시지만 " + action + "할 수 있습니다.");
        }
    }

    // 생성
    @Override
    public Message createMessage(Message message) {
        if (message == null) throw new IllegalArgumentException("메시지가 null입니다.");

        User user = userService.getUserById(message.getSender().getId());
        Channel channel = channelService.getChannelById(message.getChannel().getId());

        validateParticipant(user, channel, "작성");

        user.addMessage(message);
        channel.addChannelMessages(message);
        messageData.put(message.getId(), message);

        return message;
    }

    // 조회
    @Override
    public Message getMessageById(UUID messageId) {
        Message message = messageData.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }
        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        return messageData.values().stream()
                .toList();
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        userService.getUserById(userId);

        return messageData.values().stream()
                .filter(m -> m.getSender().getId().equals(userId))
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        channelService.getChannelById(channelId);

        return messageData.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> getMessagesByUserAndChannel(UUID userId, UUID channelId) {
        userService.getUserById(userId);
        channelService.getChannelById(channelId);

        return messageData.values().stream()
                .filter(m -> m.getSender().getId().equals(userId)
                        && m.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    // 수정
    @Override
    public Message updateMessage(UUID messageId, UUID userId, String content) {
        Message message = getMessageById(messageId);
        validateSender(message, userId, "수정");

        User user = userService.getUserById(message.getSender().getId());
        Channel channel = channelService.getChannelById(message.getChannel().getId());
        validateParticipant(user, channel, "수정");

        message.updateMessage(content);
        return message;
    }

    // 삭제
    private void removeMessage(Message message, User user, Channel channel) {
        messageData.remove(message.getId());
        user.removeMessage(message);
        channel.removeChannelMessages(message);
    }

    @Override
    public void deleteMessage(UUID messageId, UUID userId) {
        Message message = getMessageById(messageId);
        validateSender(message, userId, "삭제");

        User user = userService.getUserById(message.getSender().getId());
        Channel channel = channelService.getChannelById(message.getChannel().getId());
        validateParticipant(user, channel, "삭제");

        removeMessage(message, user, channel);
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        Channel channel = channelService.getChannelById(channelId);

        for (Message message : getMessagesByChannelId(channelId)) {
            User user = userService.getUserById(message.getSender().getId());
            removeMessage(message, user, channel);
        }
    }

    @Override
    public void deleteMessagesByUserId(UUID userId) {
        User user = userService.getUserById(userId);

        for (Message message : getMessagesByUserId(userId)) {
            Channel channel = channelService.getChannelById(message.getChannel().getId());
            removeMessage(message, user, channel);
        }

    }
}
