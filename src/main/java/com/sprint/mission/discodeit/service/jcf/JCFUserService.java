package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userData = new HashMap<>();

    private ChannelService channelService;
    private MessageService messageService;

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public User getUserById(UUID userId) {
        User user = userData.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    private void validatePassword(String origin, String entered) {
        if (!origin.equals(entered)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    public void createUser(User user) {
        if (userData.containsKey(user.getId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        userData.put(user.getId(), user);
    }

    @Override
    public void updateUsername(UUID userId, String username) {
        User user = getUserById(userId);
        user.changeUsername(username);
    }

    @Override
    public void updateEmail(UUID userId, String email) {
        User user = getUserById(userId);
        user.changeEmail(email);
    }

    @Override
    public void updatePassword(UUID userId, String originPassword, String newPassword) {
        User user = getUserById(userId);
        validatePassword(user.getPassword(), originPassword);

        user.changePassword(newPassword);
    }

    @Override
    public void deleteUser(UUID userId, String password) {
        User user = getUserById(userId);
        validatePassword(user.getPassword(), password);

        // 유저가 보낸 메시지 먼저 삭제
        List<Message> messageList = messageService.getMessagesByUserId(userId);
        messageList.stream()
                .map(Message::getId)
                .forEach(messageId -> messageService.deleteMessage(messageId));

        // 유저가 만든 채널도 삭제
        List<Channel> channelList = channelService.getChannelsByCreatorId(user.getId());
        channelList.stream()
                .map(Channel::getId)
                .forEach(channelId -> channelService.deleteChannel(channelId));

        // 마지막에 유저 삭제
        userData.remove(userId);
    }

    @Override
    public void deleteEmail(UUID userId) {
        User user = getUserById(userId);
        user.deleteEmail();
    }
}
