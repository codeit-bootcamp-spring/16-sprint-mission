package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User create(UserCreateRequest request, BinaryContentRequest profile) {
        if(data.stream().anyMatch(user -> user.getEmail().equals(request.email())))
            throw new IllegalArgumentException("사용 중인 이메일");

        User user = new User(request.name(), request.password(),request.email());

        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.stream().filter(user -> user.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Override
    public List<User> findAll() {

        return data.stream().collect(Collectors.toList());
    }

    @Override
    public User update(UUID id, String name, String password, String email) {
        User user = findById(id);
        if(data.stream().anyMatch(otherUser->!otherUser.getId().equals(id)&&otherUser.getEmail().equals(email)))
            throw new IllegalArgumentException("사용 중인 이메일");

        user.update(name, password, email);
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);

        for (Channel channel : user.getChannels()) {
            channel.getUsers().remove(user);
        }
        user.getChannels().clear();
        data.remove(user);

    }



}
