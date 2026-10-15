package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User create(User user) {
        validateEmailUnique(user.getEmail(), null);
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
    public List<User> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(user -> user.hasChannel(channelId))
                .toList();
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        User user = findById(id);
        validateEmailUnique(email, id);
        user.update(username, email, password);
        return user;

    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);

        if (!user.getChannels().isEmpty()) {
            throw new IllegalArgumentException("참여 중인 채널이 있어 삭제할 수 없습니다.");
        }
        if (!user.getMessages().isEmpty()) {
            throw new IllegalArgumentException("작성한 메시지가 있어 삭제할 수 없습니다.");
        }
        data.remove(id);
    }

    @Override
    public void addChannel(UUID id, UUID channelId) {
        findById(id).addChannel(channelId);
    }

    @Override
    public void removeChannel(UUID id, UUID channelId) {
        findById(id).removeChannel(channelId);
    }

    @Override
    public void addMessage(UUID id, UUID messageId) {
        findById(id).addMessage(messageId);
    }

    @Override
    public void removeMessage(UUID id, UUID messageId) {
        findById(id).removeMessage(messageId);
    }

    private void validateEmailUnique(String email, UUID excludeUserId) {
        for (User value : data.values()) {
            if (value.getEmail().equals(email) && !value.getId().equals(excludeUserId)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
    }
}
