package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String name) {
        User user = new User(name, new HashMap<>(), new LinkedHashMap<>());
        this.data.put(user.getId(), user);
        return user;
    }

    public User updateName(UUID id, String name) {
        User user = this.findById(id);
        if (user == null) throw new NoSuchElementException("수정할 유저가 존재하지 않습니다.");
        return user.updateName(name);
    }

    @Override
    public User findById(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        User user = this.findById(id);
        if (user == null) throw new NoSuchElementException("삭제할 유저가 존재하지 않습니다.");

        // 유저 삭제 시 채널에도 유저 삭제
        Map<UUID, Channel> channels = user.getChannels();
        channels.values().forEach(channel -> channel.removeUser(id));

        this.data.remove(id);
    }
}
