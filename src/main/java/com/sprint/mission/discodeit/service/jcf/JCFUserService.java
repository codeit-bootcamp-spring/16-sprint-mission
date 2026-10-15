package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }


    @Override
    public User create(User user) {
        isValid(user, null);

        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, User user) {
        if (!data.containsKey(id)) {
            return null;
        }

        isValid(user, id);
        data.put(id, user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        findById(id);

        for (User user : data.values()) {
            for (UUID channelId : user.getChannels()) {
                Channel channel = channelService.findById(channelId);

                if (!channel.getOwnerId().equals(user.getId())) {
                    user.removeChannel(channelId);
                }
            }
        }

        data.remove(id);
    }

    @Override
    public boolean addMessage(UUID id, UUID messageId) {
        User user = findById(id);
        return user.addMessage(messageId);
    }

    @Override
    public boolean removeMessage(UUID id, UUID messageId) {
        User user = findById(id);
        return user.removeMessage(messageId);
    }

    @Override
    public boolean addChannel(UUID id, UUID channelId) {
        User user = findById(id);

        if (user.hasChannel(channelId)) {
            throw new IllegalArgumentException("이미 가입된 채널입니다.");
        }

        return user.addChannel(channelId);
    }

    @Override
    public boolean removeChannel(UUID id, UUID channelId) {
        User user = findById(id);

        if (!user.hasChannel(channelId)) {
            throw new IllegalArgumentException("가입하지 않은 채널입니다.");
        }
        return user.removeChannel(channelId);
    }

    private void isValid(User user, UUID excludeUserId) {
        for (User value : data.values()) {
            if (value.getEmail().equals(user.getEmail())
                && !value.getId().equals(excludeUserId)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해야 합니다.");
        }

        if (user.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
    }
}
