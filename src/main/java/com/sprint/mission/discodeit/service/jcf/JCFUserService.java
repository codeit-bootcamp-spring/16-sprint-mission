package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.BaseEntity;
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

    private void validateUserId(UUID userId) {
        if (!userData.containsKey(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
    }

    private void validatePassword(String origin, String entered) {
        if (!origin.equals(entered)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    public boolean createUser(User user) {
        if (userData.containsKey(user.getId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        userData.put(user.getId(), user);
        return true;
    }

    @Override
    public boolean updateUsername(UUID userId, String username) {
        validateUserId(userId);

        User user = userData.get(userId);
        user.changeUsername(username);

        return true;
    }

    @Override
    public boolean updateEmail(UUID userId, String email) {
        validateUserId(userId);

        User user = userData.get(userId);
        user.changeEmail(email);

        return true;
    }

    @Override
    public boolean updatePassword(UUID userId, String originPassword, String newPassword) {
        validateUserId(userId);

        User user = userData.get(userId);
        validatePassword(user.getPassword(), originPassword);

        user.changePassword(newPassword);
        return true;
    }

    @Override
    public boolean deleteUser(UUID userId, String password) {
        validateUserId(userId);

        User user = userData.get(userId);
        validatePassword(user.getPassword(), password);

        // 해당 사용자가 만든 채널 확인
        // 해당 채널의 메시지들 삭제 (deleteChannel() 안에 구현돼있음)
        // 해당 채널 삭제
        List<Channel> channelList = channelService.getChannelsByUserId(user.getId());
        channelList.stream()
                .map(BaseEntity::getId)
                .forEach(channelId -> channelService.deleteChannel(channelId));

        // 해당 사용자의 메시지 확인 후 삭제
        List<Message> messageList = messageService.getMessagesByUserId(userId);
        messageList.stream()
                .map(BaseEntity::getId)
                .forEach(messageId -> messageService.deleteMessage(messageId));

        userData.remove(userId);
        return true;
    }

    @Override
    public void getUserById(UUID userId) {
        validateUserId(userId);

        userData.get(userId);
    }
}
