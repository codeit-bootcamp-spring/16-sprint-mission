package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public User create(String name, String password, String email) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<User> users = entities.getUsers();

        if(users.stream().anyMatch(u -> u.getEmail().equals(email))){
            throw new IllegalArgumentException("사용 중인 이메일");
        }

        User user = new User(name, password, email);
        users.add(user);
        entities.saveAll();
        return user;
    }

    @Override
    public User findById(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        return entities.getUsers().stream().filter(u -> u.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
    }

    @Override
    public List<User> findAll() {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        return entities.getUsers();
    }

    @Override
    public User update(UUID id, String name, String password, String email) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<User> users = entities.getUsers();

        User user = users.stream().filter(u -> u.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(users.stream().anyMatch(otherUser ->
                !otherUser.getId().equals(id)&&otherUser.getEmail().equals(email))){
            throw new IllegalArgumentException("사용 중인 이메일");
        }

        user.update(name, password, email);
        entities.saveAll();
        return user;
    }

    @Override
    public void delete(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<User> users = entities.getUsers();

        User user = users.stream().filter(u -> u.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        users.remove(user);
        entities.saveAll();
    }
}